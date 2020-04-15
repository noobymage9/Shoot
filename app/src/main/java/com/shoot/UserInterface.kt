package com.shoot

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.shoot.engine.DynamicData
import com.shoot.engine.Entity
import com.shoot.engine.Main
import com.shoot.engine.Main.Companion.player
import com.shoot.engine.Player

class UserInterface @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var differenceX : Float = 0f
    var differenceY : Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                differenceX = event.x - player.position.x
                differenceY = event.y - player.position.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                player.position = Entity.Position(event.x - differenceX, event.y - differenceY)
                if (PlayFragment.twoPlayer) sendToOther()
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (player.resetted) {
                    player.reset()
                    if (PlayFragment.twoPlayer) sendToOther()
                    player.resetted = false
                }
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun sendToOther() {
        val temp = player.dynamicData.clone().update(player.position)
        val playerData = Payload.fromBytes(MainActivity.serialize(temp.inverse().toRatio()))
        PlayFragment.endPointID?.let {
            Nearby.getConnectionsClient(context).sendPayload(
                it, playerData)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        player.draw(canvas)
        Main.enemy.draw(canvas)
        for (bots in player.bots) bots.draw(canvas)
        for (bots in Main.enemy.bots) {
            bots.draw(canvas)
        }
        for (bullet in player.bullets) bullet.draw(canvas)
        for (bullet in Main.enemy.bullets) {
            bullet.draw(canvas)
        }
        super.onDraw(canvas)
        invalidate()
    }
}