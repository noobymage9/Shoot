package com.shoot

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.shoot.engine.Enemy
import com.shoot.engine.Main
import com.shoot.engine.Main.Companion.enemy
import com.shoot.engine.Main.Companion.player
import com.shoot.engine.Player
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.android.synthetic.main.fragment_play.view.*
import kotlinx.android.synthetic.main.view_countdown.view.*
import kotlinx.android.synthetic.main.view_enemy_selection.*
import kotlinx.android.synthetic.main.view_enemy_selection.view.*
import kotlinx.android.synthetic.main.view_mode_selection.*
import kotlinx.android.synthetic.main.view_mode_selection.view.*
import kotlinx.android.synthetic.main.view_result.view.*


class PlayFragment : Fragment() {
    var step = 1
    var main : Main? = null
    var engine : Thread? = null
    lateinit var root : View
    var endPoints = HashMap<String, String>()
    val search = Runnable {
        startAdvertising()
        startDiscovery()
    }
    val loadUser = Runnable {
        val db = context?.let { DB.newInstance(it) }
        user = db?.userDao()?.getUser()
    }
    var user : User? = null
    val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endPointID : String, discoveredEndpointInfo : DiscoveredEndpointInfo) {
            if (endPoints.put(endPointID, discoveredEndpointInfo.endpointName) == null && !discoveredEndpointInfo.endpointName.equals("")) {
                (enemy_list.adapter as EnemyAdapter).enemies.add(EnemyInformation(discoveredEndpointInfo.endpointName, endPointID))
                (enemy_list.adapter as EnemyAdapter).notifyDataSetChanged()
            }
        }

        override fun onEndpointLost(endPointID: String) {
            val name = endPoints.get(endPointID)
            endPoints.remove(endPointID)
            (enemy_list.adapter as EnemyAdapter).enemies.remove(EnemyInformation(name!!, endPointID))
            (enemy_list.adapter as EnemyAdapter).notifyDataSetChanged()
        }
    }

    val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointID: String, p1: ConnectionInfo) {
            val connectRequestDialog = context?.let { AlertDialog.Builder(it) }
            connectRequestDialog?.setTitle(R.string.connection_request)
            val requestDetails = p1.endpointName + resources.getString(R.string.connection_request_details)
            connectRequestDialog?.setMessage(requestDetails)
            connectRequestDialog?.setPositiveButton(R.string.accept, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Nearby.getConnectionsClient(context!!)
                        .acceptConnection(endPointID, DataTransfer())

                }
            })
            connectRequestDialog?.setNegativeButton(R.string.reject, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Nearby.getConnectionsClient(context!!)
                        .rejectConnection(endPointID)
                }
            })
            connectRequestDialog?.show()
        }
        override fun onConnectionResult(endPointID: String, result: ConnectionResolution) {
            when (result.getStatus().getStatusCode()) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    step = 0
                    twoPlayer = true
                    PlayFragment.endPointID = endPointID
                    stopAdvertising()
                    stopDiscovery()
                    if (!PlayFragment.host) {
                        player = Player()
                        val playerData = Payload.fromBytes(MainActivity.serialize(player.clone().inverse().toRatio()))
                        context?.let { Nearby.getConnectionsClient(it).sendPayload(endPointID, playerData) }
                        enemy = Enemy()
                    }
                    MainActivity.fadeOut(root.player_selection)
                    val temp = ArrayList<View>()
                    temp.add(user_interface)
                    temp.add(root.one)
                    temp.add(root.two)
                    temp.add(root.three)
                    countdown(temp, temp.size - 1, ::startGame)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                }
                else -> {
                }
            }
        }


        override fun onDisconnected(p0: String) {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        PlayFragment.host = true
        endPointID = null
        (activity as MainActivity).enterImmersiveMode()
        (activity as MainActivity).registerSystemUIListener()
        endPoints = HashMap()
        root = inflater.inflate(R.layout.fragment_play, container, false)
        root.one_player.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                twoPlayer = false
                step = 0
                MainActivity.fadeOut(mode_selection)
                val temp = ArrayList<View>()
                temp.add(user_interface)
                temp.add(root.one)
                temp.add(root.two)
                temp.add(root.three)
                countdown(temp, temp.size - 1, ::startGame)
            }
        })
        root.two_player.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                step = 2
                MainActivity.fadeOut(root.mode_selection)
                MainActivity.fadeIn(root.player_selection)
                Thread(search).start()
            }
        })
        root.back_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                step = 1
                MainActivity.fadeOut(root.player_selection)
                MainActivity.fadeIn(root.mode_selection)
            }
        })
        root.result.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                step = 1
                MainActivity.fadeOut(root.result)
                MainActivity.fadeIn(root.mode_selection)
            }
        })

        root.enemy_list.adapter = EnemyAdapter(ArrayList(), this)
        Thread(loadUser).start()
        return root
    }

    private fun startDiscovery() {
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        Nearby.getConnectionsClient(context!!)
            .startDiscovery(MainActivity.SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: java.lang.Exception? -> }
    }

    private fun startAdvertising() {
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        if (user != null) {
            Log.e("ADVERTISING", user!!.name)
            Nearby.getConnectionsClient(context!!)
                .startAdvertising(
                    user!!.name, MainActivity.SERVICE_ID, connectionLifecycleCallback, advertisingOptions
                )
                .addOnSuccessListener { unused: Void? -> }
                .addOnFailureListener { e: Exception? -> }
        }
    }

    private fun stopAdvertising() {
        Nearby.getConnectionsClient(context!!).stopAdvertising()
    }

    private fun stopDiscovery() {
        Nearby.getConnectionsClient(context!!).stopDiscovery()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        stopGame()
    }

    private fun startGame() {
        if (PlayFragment.host) {
            main = Main.newInstance(this)
            engine = Thread(main)
            engine?.start()
        }

    }


    fun stopGame() {
        main?.terminate()
        engine?.interrupt()
        Main.reset()
    }

    fun displayResult(winner: String, timeElapsed : ArrayList<Int>) {
        step = 3
        MainActivity.fadeOut(root.user_interface)
        val timeText = timeElapsed.get(0).toString() + "mins " + timeElapsed.get(1).toString() + "s"
        root.victory_defeat.text = winner
        root.time_taken.text = timeText
        MainActivity.fadeIn(root.result)

        stopGame()

    }

    fun connect(targetID : String) {
        stopAdvertising()
        stopDiscovery()
        Nearby.getConnectionsClient(context!!)
            .requestConnection(user!!.name, targetID, connectionLifecycleCallback)
            .addOnSuccessListener { unused: Void? -> PlayFragment.host = false}
            .addOnFailureListener { e: java.lang.Exception? -> }
    }

    companion object {
        var twoPlayer = false
        var host = true
        var endPointID : String? = null
        val KEY = "play_fragment"
        val COUNTDOWN_DURATION = 500L
        fun newInstance() = PlayFragment()

        fun countdown(views: ArrayList<View>, index: Int, trigger: () -> Unit){
            if (index == 0) {
                MainActivity.fadeIn(views.get(index), trigger)
                return
            }
            views.get(index).animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        views.get(index).visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        views.get(index).animate()
                            .alpha(0.0f)
                            .setListener(object : AnimatorListenerAdapter(){
                                override fun onAnimationStart(animation: Animator?) {
                                    super.onAnimationStart(animation)
                                    views.get(index).visibility = View.GONE
                                }
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    countdown(views, index - 1, trigger)
                                }
                            })
                            .duration = COUNTDOWN_DURATION
                    }
                })
                .duration = COUNTDOWN_DURATION
        }
    }
}
