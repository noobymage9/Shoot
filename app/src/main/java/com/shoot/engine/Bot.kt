package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.shoot.engine.Main.Companion.enemy
import com.shoot.engine.Main.Companion.player
import java.io.Serializable

class Bot(position: Position, val botType: BotType, override var bulletType: BulletType, val TAG : String) : Entity(botType.size, position, botType.movement), Serializable, Cloneable, Shootable{


    var hp = botType.hp
    var TTL = botType.TTL
    override var baseShootRate= botType.baseShootRate
    override var shootVariance = botType.shootVariance
    override var shootRate = this.calculateShootRate()
    override fun shoot() {
        when (TAG) {
            "player" -> {
                player.bullets.add(Bullet(
                    position.clone(),
                    bulletType.clone(),
                    TAG
                ))
            }
            "enemy" -> {
                enemy.bullets.add(Bullet(
                    position.clone(),
                    bulletType.clone(),
                    TAG
                ))
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.drawRect(position.x - size, position.y - size, position.x + size, position.y + size, sprite)
    }

    fun isDead(): Boolean {
        return hp <= 0 || TTL <= 0
    }
    fun damage() = hp--

    public override fun clone() = Bot(position.clone(), botType.clone(), bulletType.clone(), TAG)

    override fun move() {
        TTL--
        super.move()
    }

    companion object {
        var sprite = Paint().apply{
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }


}