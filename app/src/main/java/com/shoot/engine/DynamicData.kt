package com.shoot.engine

import com.shoot.MainActivity
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

data class DynamicData(
    var bots: CopyOnWriteArrayList<Bot>?,
    var bullets: CopyOnWriteArrayList<Bullet>?,
    var position: Entity.Position?,
    var TAG: String
) : Serializable, Cloneable {

    var converted = false


    fun update(
        bots: CopyOnWriteArrayList<Bot>?,
        bullets: CopyOnWriteArrayList<Bullet>?,
        position: Entity.Position?,
        TAG: String
    ) : DynamicData {
        this.bots = bots
        this.bullets = bullets
        this.position = position
        this.TAG = TAG
        return this
    }

    // ALWAYS INVERSE FIRST BEFORE CONVERTING
    fun inverse(): DynamicData {
        TAG = if (TAG.equals("enemy")) "player"
        else "enemy"
        position?.inverse()
        if (bots != null)
            for (bot in bots!!) bot.position.inverse()

        if (bullets != null) {
            for (bullet in bullets!!) {
                bullet.position.inverse()
                bullet.movement.inverse()
            }
        }
        return this
    }

    fun toRatio(): DynamicData {
        position?.toRatio()
        if (bots != null)
            for (bot in bots!!) bot.position.toRatio()
        if (bullets != null)
            for (bullet in bullets!!) bullet.position.toRatio()
        return this
    }

    fun toValue(): DynamicData {
        position?.toValue()
        if (bots != null)
            for (bot in bots!!) bot.position.toValue()
        if (bullets != null)
            for (bullet in bullets!!) bullet.position.toValue()
        return this
    }

    public override fun clone(): DynamicData {
        var tempBots: CopyOnWriteArrayList<Bot>?
        var tempBullets: CopyOnWriteArrayList<Bullet>?
        val position = position?.x?.let { position?.y?.let { it1 -> Entity.Position(it, it1) } }
        if (bots != null) {
            tempBots = CopyOnWriteArrayList()
            for (bot in bots!!) tempBots.add(bot.clone())
        }
        else
            tempBots = null
        if (bullets != null) {
            tempBullets = CopyOnWriteArrayList()
            for (bullet in bullets!!) tempBullets.add(bullet.clone())
        }
        else
            tempBullets = null
        return DynamicData(tempBots, tempBullets, position, String(TAG.toCharArray()))
    }

    fun removeBots(): DynamicData{
        bots = null
        return this
    }
    fun removeBullets(): DynamicData {
        bullets = null
        return this
    }
}
