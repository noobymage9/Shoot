package com.shoot.engine

import java.io.Serializable

data class BotType(var size: Float, var movement: Entity.Movement, var hp: Int, var TTL: Int, var baseShootRate: Int, var shootVariance: Int) :
    Serializable, Cloneable {

    public override fun clone() = BotType(size, movement.clone(), hp, TTL, baseShootRate, shootVariance)
}
