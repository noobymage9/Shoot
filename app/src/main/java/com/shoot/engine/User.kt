package com.shoot.engine

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import java.io.Serializable
import java.util.concurrent.CopyOnWriteArrayList

abstract class User(var colour: String, position: Position, val TAG: String): Entity(size = 50f, position = position), Serializable, Shootable, BotSpawnable {
    var initialLife = DEFAULT_INITIAL_LIFE
    var resetted = false
    var bots = CopyOnWriteArrayList<Bot>()
    var bullets = CopyOnWriteArrayList<Bullet>()
    var invulnerableDuration = DEFAULT_INVULNERABILITY_DURATION
    var invulnerableTime = 0
    var life = initialLife

    // Shoot Parameters
    override var bulletType = BulletEnum.getValueOf(DEFAULT_BULLET_TYPE)
    override var baseShootRate = DEFAULT_BASE_SHOOT_RATE
    override var shootVariance = DEFAULT_SHOOT_VARIANCE
    override var shootRate = this.calculateShootRate()

    // Bot Parameters
    override var botBulletType = BulletEnum.getValueOf(DEFAULT_BOT_BULLET_TYPE)
    override var botType = BotEnum.getValueOf(DEFAULT_BOT_TYPE)
    override var baseSpawnRate = DEFAULT_BASE_SPAWN_RATE
    override var spawnVariance = DEFAULT_SPAWN_VARIANCE
    override var spawnRate = this.calculateSpawnRate()
    override var spawnRadius = DEFAULT_SPAWN_RADIUS

    override fun shoot() {
        bullets.add(Bullet(
            position.clone(),
            bulletType.clone(),
            TAG
        ))
    }

    override fun spawnBot() {
        bots.add(
            Bot(
                Position(IntRange((position.x - spawnRadius).toInt(), (position.x + spawnRadius).toInt()).random().toFloat()
                    , IntRange((position.y - spawnRadius).toInt(), (position.y + spawnRadius).toInt()).random().toFloat()),
                botType.clone(),
                botBulletType.clone(),
                TAG
            )
        )
    }


    open fun reset() {
        bullets = CopyOnWriteArrayList()
        bots = CopyOnWriteArrayList()
        invulnerableTime = 0
        life = initialLife
    }

    fun damaged() {
        life--
        invulnerableTime = invulnerableDuration
    }

    fun isDead() = life <= 0

    fun isInvulnerable() = invulnerableTime > 0

    fun update(temp: DynamicData) {
        if (temp.bots != null) this.bots = temp.bots!!
        if (temp.bullets != null) this.bullets = temp.bullets!!
        if (TAG.equals("enemy")) this.position = temp.position!!
    }


    override fun draw(canvas: Canvas?) {
        var visible = true
        if (this.isInvulnerable())
            if (this.invulnerableTime % DEFAULT_FLICKER_FREQUENCY in (0..4)) visible = false
        if (visible) canvas?.drawCircle(position.x, position.y, size, sprite)
    }

    @Transient
    var sprite = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor(colour)
        style = Paint.Style.FILL
    }

    var dynamicData = DynamicData(bots, bullets, position, TAG)
        get() {
            if (TAG.equals("player")) return field.update(bots, bullets, position, TAG)
            else return field.update(bots, bullets, null, TAG)
        }

    companion object{
        const val DEFAULT_INITIAL_LIFE = 3
        const val DEFAULT_INVULNERABILITY_DURATION = 50
        const val DEFAULT_FLICKER_FREQUENCY = 10
        const val DEFAULT_BULLET_TYPE = "default"
        const val DEFAULT_BASE_SHOOT_RATE = 50
        const val DEFAULT_SHOOT_VARIANCE = 25
        const val DEFAULT_BOT_TYPE = "default"
        const val DEFAULT_BOT_BULLET_TYPE = "bot_default"
        const val DEFAULT_BASE_SPAWN_RATE = 50
        const val DEFAULT_SPAWN_VARIANCE = 25
        const val DEFAULT_SPAWN_RADIUS = 100
    }

}