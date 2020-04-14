package com.shoot.engine

import java.io.Serializable

enum class BulletEnum(var size: Float, var movement: Entity.Movement): Serializable{

    DEFAULT(25f, Entity.Movement(0f, 10f)),
    BOT_DEFAULT(15f, Entity.Movement(0f, 10f)),
    EMPTY(0f, Entity.Movement(0f, 0f));


    companion object{
        fun getValueOf(type: String) : BulletType{
            when (type) {
                "default" -> return clone(DEFAULT)
                "bot_default" -> return clone(BOT_DEFAULT)
            }
            return clone(EMPTY)
        }
        fun clone(bulletEnum: BulletEnum) = BulletType(bulletEnum.size, bulletEnum.movement.clone())
    }
}
