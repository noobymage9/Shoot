package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import com.shoot.MainActivity
import java.io.Serializable


abstract class Entity(var size: Float = DEFAULT_SIZE, open var position: Position = Position(DEFAULT_X, DEFAULT_Y), val movement: Movement = Movement(
    DEFAULT_XVEL, DEFAULT_YVEL)) : Serializable {

    abstract fun draw(canvas: Canvas?)
    open fun move() {
        position.x -= movement.xVel
        position.y -= movement.yVel
    }

    data class Position(var x : Float, var y : Float) : Serializable, Cloneable{
        fun inverse(): Position {
            x = MainActivity.width - x
            y = MainActivity.width - y
            return this
        }
        fun reset(TAG: String) {
            if (TAG.equals("player")) {
                x = MainActivity.width / 2f
                y = 6* MainActivity.height / 8f
            } else {
                x = MainActivity.width / 2f
                y = 2* MainActivity.height / 8f
            }
        }
        fun toRatio() {
            x = x / MainActivity.width
            y = y / MainActivity.height
        }
        fun toValue(): Position {
            x = x * MainActivity.width
            y = y * MainActivity.width
            return this
        }
        public override fun clone(): Position = Position(x, y)
    }
    data class Movement(var xVel : Float, var yVel : Float): Serializable, Cloneable {
        fun inverse() {
            xVel = -xVel
            yVel = -yVel
        }

        public override fun clone() = Movement(xVel, yVel)
    }


    companion object {
        const val DEFAULT_SIZE = 10f
        const val DEFAULT_X = 0f
        const val DEFAULT_Y = 0f
        const val DEFAULT_XVEL = 0f
        const val DEFAULT_YVEL = 0f
    }
}