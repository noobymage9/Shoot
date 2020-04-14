package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.shoot.MainActivity
import com.shoot.PlayFragment
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random
import kotlin.random.nextInt

class Enemy : User(colour = DEFAULT_COLOR, position = DEFAULT_POSITION, TAG = TAG){

    fun set(player: Player) {
        bulletType = player.bulletType
        botBulletType = player.botBulletType
        botType = player.botType
        baseShootRate = player.baseShootRate
        baseSpawnRate = player.baseSpawnRate
        shootVariance = player.shootVariance
        spawnVariance = player.spawnVariance
        shootRate = player.shootRate
        spawnRate = player.spawnRate
        initialLife = player.initialLife
        dynamicData = player.dynamicData
        life = player.initialLife
        colour = player.colour
        bots = player.bots
        bullets = player.bullets
        invulnerableDuration = player.invulnerableDuration
        position = player.position
    }

    fun inverse() {
        bulletType.movement.inverse()
        botType.movement.inverse()
        botBulletType.movement.inverse()
    }

    companion object {
        val DEFAULT_COLOR = "#BB457A"
        val DEFAULT_POSITION = Position(MainActivity.width / 2f, 2* MainActivity.height / 8f)
        val TAG = "enemy"
    }

}
