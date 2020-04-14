package com.shoot.engine

import java.io.Serializable

data class BulletType(var size: Float, var movement: Entity.Movement): Serializable, Cloneable {
    public override fun clone() = BulletType(size, movement.clone())
}
