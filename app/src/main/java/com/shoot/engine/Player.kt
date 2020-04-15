package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.shoot.MainActivity
import com.shoot.engine.Main.Companion.player
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList


class Player: User(colour = "#45B0BB", position = Position(MainActivity.width / 2f, 6* MainActivity.height / 8f), TAG = "player"), Serializable, Cloneable{

    val UPPER_MOVEMENT_LIMIT = MainActivity.height / 2 + size
    val LOWER_MOVEMENT_LIMIT = MainActivity.height - size
    val LEFT_MOVEMENT_LIMIT = size
    val RIGHT_MOVEMENT_LIMIT = MainActivity.width - size
    var converted = false

    override var position: Position = Position(MainActivity.width / 2f, 6* MainActivity.height / 8f)
        get() = field
        set(value) {
        limitPositionalValues(value)
        field = value
    }

    private fun limitPositionalValues(value: Position) {
        value.x = if (value.x < LEFT_MOVEMENT_LIMIT) LEFT_MOVEMENT_LIMIT
        else if (value.x > RIGHT_MOVEMENT_LIMIT) RIGHT_MOVEMENT_LIMIT
        else value.x
        value.y = if (value.y < UPPER_MOVEMENT_LIMIT) UPPER_MOVEMENT_LIMIT
        else if (value.y > LOWER_MOVEMENT_LIMIT) LOWER_MOVEMENT_LIMIT
        else value.y
    }

    override fun reset() {
        super.reset()
        position.reset(TAG)
    }

    public override fun clone(): Player{
        val temp = Player()
        temp.bulletType = bulletType.clone()
        temp.botBulletType = botBulletType.clone()
        temp.botType = botType.clone()
        temp.baseShootRate = baseShootRate
        temp.baseSpawnRate = baseSpawnRate
        temp.shootVariance = shootVariance
        temp.spawnVariance = spawnVariance
        temp.shootRate = shootRate
        temp.spawnRate = spawnRate
        temp.initialLife = initialLife
        temp.life = initialLife
        temp.colour = colour
        temp.invulnerableDuration = invulnerableDuration
        temp.position = position
        return temp
    }


    fun inverse(): Player {
        bulletType.movement.inverse()
        botType.movement.inverse()
        botBulletType.movement.inverse()
        position.inverse()
        return this
    }

    fun toRatio(): Player{

        position.toRatio()
        return this
    }

    fun toValue(): Player {
        position.toValue()
        return this
    }


}