package com.shoot.engine

import android.util.Log
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.shoot.MainActivity
import com.shoot.PlayFragment
import com.shoot.UserInterface
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.random.Random

class Main private constructor(val playFragment: PlayFragment): Runnable {
    var winner = ""
        get() = field
    val FRAME_RATE : Long = 1000 / 60L
    var time = 0
        get() = field
    var done = false
    var running = false

    fun terminate() {
        running = false
        done = true
    }

    override fun run() {
        val startTime = System.currentTimeMillis()
        running = true
        winner = ""
        if (!PlayFragment.twoPlayer) {
            enemy.inverse()
        }
        while (running) {

            val bullets = initialiseBullets()
            val bots = initialiseBots()
            time++
            if (enemy.isInvulnerable()) enemy.invulnerableTime--
            if (player.isInvulnerable()) player.invulnerableTime--
            if (time % player.shootRate == 0) {
                player.shoot()
            }
            if (time % enemy.shootRate == 0) {
                enemy.shoot()
            }
            if (time % player.spawnRate == 0) {
                player.spawnBot()
            }
            if (time % enemy.spawnRate == 0) {
                enemy.spawnBot()
            }

            for (bot in bots) {
                bot.move()
                if (time % bot.shootRate == 0) bot.shoot()
            }
            for (bullet in bullets) {
                bullet.move()
            }
            remove(player.bullets)
            remove(enemy.bullets)
            for (bullet in bullets) {
                var collided = false
                val playerBullet = bullet.TAG.equals("player")
                if (playerBullet) {
                    if (bullet.collide(enemy)) {
                        if (!enemy.isInvulnerable()) enemy.damaged()
                        collided = true
                    }
                } else
                    if (bullet.collide(player) && !player.isInvulnerable()) {
                        if (!player.isInvulnerable()) player.damaged()
                        collided = true
                    }
                for (bot in bots) {
                    val playerBot = bot.TAG.equals("player")
                    if (!playerBullet && playerBot)
                        if (bullet.collide(bot)) {
                            bot.damage()
                            collided = true
                        }
                    if (playerBullet && !playerBot) {
                        if (bullet.collide(bot)) {
                            bot.damage()
                            collided = true
                        }
                    }
                }
                if (collided) bullet.isUsed = true
            }
            remove(player.bots)
            remove(enemy.bots)
            checkVictory()
            try {
                Thread.sleep(FRAME_RATE)
            } catch (interruptedException : InterruptedException) {
                running = false
                done = true
                Thread.currentThread().interrupt()
            }
            if (!winner.equals("")) break

            if (PlayFragment.twoPlayer) {
                val playerData = Payload.fromBytes(MainActivity.serialize(player.dynamicData.clone().update(player.position).update(player.bots).update(player.bullets).inverse().toRatio()))
                val enemyData = Payload.fromBytes(MainActivity.serialize(enemy.dynamicData.clone().update(enemy.position).update(enemy.bots).update(enemy.bullets).inverse().toRatio()))
                PlayFragment.endPointID?.let {
                    Nearby.getConnectionsClient(playFragment.context!!).sendPayload(
                        it, playerData)
                }
                PlayFragment.endPointID?.let {
                   Nearby.getConnectionsClient(playFragment.context!!).sendPayload(
                       it, enemyData)
                }

            }
        }
        val differenceInTime = System.currentTimeMillis() - startTime
        val timeElapsed = formatTime(differenceInTime)
        done = true
        playFragment.displayResult(winner, timeElapsed)
    }

    private fun initialiseBots(): ArrayList<Bot> {
        val temp = ArrayList<Bot>()
        val tempPlayerBots = ArrayList<Bot>()
        val tempEnemyBots = ArrayList<Bot>()
        tempPlayerBots.addAll(player.bots)
        tempEnemyBots.addAll(enemy.bots)
        while (tempPlayerBots.size != 0 || tempEnemyBots.size != 0) {

            if (Random.nextBoolean()) {
                try {
                    temp.add(tempPlayerBots.removeAt(0))
                } catch (exception : IndexOutOfBoundsException) {
                    while (tempEnemyBots.size != 0 ) temp.add(tempEnemyBots.removeAt(0))
                }
            } else {
                try {
                    temp.add(tempEnemyBots.removeAt(0))
                } catch (exception : IndexOutOfBoundsException) {
                    while (tempPlayerBots.size != 0) temp.add(tempPlayerBots.removeAt(0))
                }
            }
        }
        return temp
    }

    private fun initialiseBullets(): ArrayList<Bullet> {
        val temp = ArrayList<Bullet>()
        val tempPlayerBullets = ArrayList<Bullet>()
        val tempEnemyBullets = ArrayList<Bullet>()
        tempPlayerBullets.addAll(player.bullets)
        tempEnemyBullets.addAll(enemy.bullets)
        while (tempPlayerBullets.size != 0 || tempEnemyBullets.size != 0) {
            if (Random.nextBoolean()) {
                try {
                    temp.add(tempPlayerBullets.removeAt(0))
                } catch (exception : IndexOutOfBoundsException) {
                    while (tempEnemyBullets.size != 0) temp.add(tempEnemyBullets.removeAt(0))
                }
            } else {
                try {
                    temp.add(tempEnemyBullets.removeAt(0))
                } catch (exception : IndexOutOfBoundsException) {
                    while (tempPlayerBullets.size != 0) temp.add(tempPlayerBullets.removeAt(0))
                }
            }
        }
        return temp
    }

    private fun formatTime(differenceInTime: Long): ArrayList<Int> {
        val seconds = (differenceInTime / 1000).toInt()
        val minutes = (seconds / 60)
        val temp = ArrayList<Int>()
        temp.add(minutes)
        temp.add(seconds)
        return temp
    }

    private fun checkVictory() {
        if (enemy.isDead()) winner = "player"
        if (player.isDead()) winner = "enemy"
    }

    private fun remove(arrayList: CopyOnWriteArrayList<*>) {
        val tempArrayList = CopyOnWriteArrayList<Any>()
        for (item in arrayList) {
            when (item) {
                is Bot -> if (item.isDead()) {
                    tempArrayList.add(item)
                }
                is Bullet -> if (item.isUsed || item.exit()) tempArrayList.add(item)
            }
        }
        arrayList.removeAll(tempArrayList)
    }

    companion object {
        lateinit var current : Main

        var enemy = Enemy()
        var player = Player()

        fun newInstance(playFragment: PlayFragment) : Main? {
            if (!::current.isInitialized || current.done) {
                current = Main(playFragment)
                current.done = false
                return current
            }
            return null
        }

        fun reset() {
            enemy = Enemy()
            player.reset()
            player.resetted = true
        }



    }

}