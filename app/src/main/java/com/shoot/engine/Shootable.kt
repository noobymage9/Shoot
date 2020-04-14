package com.shoot.engine

import kotlin.random.Random
import kotlin.random.nextInt

interface Shootable {
    var bulletType: BulletType
    var baseShootRate: Int
    var shootVariance: Int
    var shootRate: Int
    fun calculateShootRate() = Random.nextInt(baseShootRate until baseShootRate + shootVariance)
    fun shoot()
}