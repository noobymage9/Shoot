package com.shoot.engine

import java.io.Serializable
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.random.nextInt

enum class BotEnum(var size: Float, var movement: Entity.Movement, var hp: Int, var TTL: Int, var baseShootRate: Int, var shootVariance: Int){

    DEFAULT(15f, Entity.Movement(0f, 0f), 5, 200, 25, 5),
    EMPTY(0f, Entity.Movement(0f, 0f), 0, 0, 0, 0);

    companion object{
        fun getValueOf(type: String): BotType{
            when(type) {
                "default" -> return clone(DEFAULT)
            }
            return clone(EMPTY)
        }
        fun clone(botEnum: BotEnum) = BotType(botEnum.size, botEnum.movement.clone(), botEnum.hp, botEnum.TTL, botEnum.baseShootRate, botEnum.shootVariance)

}





}