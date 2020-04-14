package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.shoot.MainActivity
import java.io.Serializable

class Bullet(position: Position, var bulletType: BulletType, val TAG: String) : Entity(bulletType.size, position, bulletType.movement), Serializable, Cloneable{
    var isUsed = false


    override fun draw(canvas: Canvas?) {
        canvas?.drawCircle(position.x, position.y, size, sprite)
    }


    fun exit(): Boolean {
        if (position.x + size < 0 || position.x + size > MainActivity.width) return true
        if (position.y + size < 0 || position.y + size > MainActivity.height) return true
        return false
    }

    fun collide(entity: Entity) : Boolean {
        val directDistance = Math.sqrt(Math.pow((entity.position.x - position.x).toDouble(), 2.0) + Math.pow((entity.position.y - position.y).toDouble(), 2.0))
        return directDistance < entity.size + size
    }

    public override fun clone() = Bullet(position.clone(), bulletType.clone(), TAG)

    companion object {
        @Transient
        var sprite = Paint().apply{
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

    }
}