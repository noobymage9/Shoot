package com.shoot.engine

import kotlin.random.Random
import kotlin.random.nextInt

interface BotSpawnable {
    var botBulletType: BulletType
    var botType: BotType
    var baseSpawnRate: Int
    var spawnVariance: Int
    var spawnRate: Int
    var spawnRadius: Int
    fun calculateSpawnRate() = Random.nextInt(baseSpawnRate until baseSpawnRate + spawnVariance)
    fun spawnBot()
}