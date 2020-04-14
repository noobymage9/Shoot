package com.shoot

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    val displayMetrics = DisplayMetrics()
    val GOOGLE_API_REQUEST = 1
    private val handler = Handler()
    private lateinit var neededPermissions: Array<String>
    private val ALL_PERMISSIONS_REQUEST = 2
    private val PERMISSIONS_LIST = arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)


    private val immersiveModeRunnable = Runnable {
        enterImmersiveMode()
    }
    private val navigationOutRunnable = Runnable {
        slideOut(navigation)
    }
    private val navigationItemListener = BottomNavigationView.OnNavigationItemSelectedListener {
        var itemPressed = false
        handler.removeCallbacks(navigationOutRunnable)
        when(it.itemId) {
            R.id.action_customise -> {
                val customiseFragment = CustomiseFragment.newInstance()
                loadFragment(customiseFragment, CustomiseFragment.KEY)

                itemPressed = true
            }
            R.id.action_play -> {
                val playFragment = PlayFragment.newInstance()
                loadFragment(playFragment, PlayFragment.KEY)
                itemPressed = true
            }
            R.id.action_setting -> {
                val settingFragment = SettingFragment.newInstance()
                    loadFragment(settingFragment, SettingFragment.KEY)
                itemPressed = true
            }
        }
        itemPressed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var navigationBarHeight = 0
        val resourceId =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        height = displayMetrics.heightPixels + navigationBarHeight
        width = displayMetrics.widthPixels
        navigation.setOnNavigationItemSelectedListener(navigationItemListener)
        navigation.selectedItemId = R.id.action_play

        if (!allPermissionsEnabled()) ActivityCompat.requestPermissions(
            this,
            neededPermissions,
            ALL_PERMISSIONS_REQUEST
        )

    }

    private fun allPermissionsEnabled(): Boolean {
        var temp = true
        val tempList = ArrayList<String>()
        for (permission in PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                temp = false
                tempList.add(permission)
            }
        }
        neededPermissions = tempList.toTypedArray()
        return temp
    }


    fun loadFragment(fragment: Fragment, key : String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(fragment_container.id, fragment, key)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun finish() {
        (supportFragmentManager.findFragmentByTag(PlayFragment.KEY) as PlayFragment).stopGame()
        super.finish()
    }
    fun enterImmersiveMode() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        handler.postDelayed(navigationOutRunnable, 4000)
    }

    fun registerSystemUIListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                slideIn(navigation)
                handler.postDelayed(immersiveModeRunnable, 2000)
            } else {
                handler.postDelayed(navigationOutRunnable, 4000)
            }
        }
    }

    fun deRegisterSystemUIListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener(null)
    }

    override fun onPause() {
        super.onPause()
        deRegisterSystemUIListener()
        handler.removeCallbacksAndMessages(null)
    }



    override fun onResume() {
        super.onResume()
        registerSystemUIListener()
        handler.postDelayed(immersiveModeRunnable, 2000)
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(applicationContext)
        if (result != ConnectionResult.SUCCESS) {
            googleApiAvailability.showErrorDialogFragment(this, result, GOOGLE_API_REQUEST, DialogInterface.OnCancelListener {
                val no2PAlertDialog = AlertDialog.Builder(this)
                no2PAlertDialog.setTitle(R.string.no2PAlertTitle)
                no2PAlertDialog.setMessage(R.string.no2PDetails)
                no2PAlertDialog.setNeutralButton(R.string.solo) { dialog, _ ->  dialog.dismiss()
                }
                no2PAlertDialog.show()
            })
        }
    }

    companion object {
        var height by Delegates.notNull<Int>()
        var width by Delegates.notNull<Int>()
        val SERVICE_ID = "com.shoot.noobymage9"

        fun fadeIn(view: View) {
            view.animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }
                })
        }

        fun fadeIn(view: View, trigger: () -> Unit) {
            view.animate()
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        trigger()
                    }
                })
        }

        fun fadeOut(view: View) {
            view.animate()
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
        }

        fun slideIn(view: View) {
            view.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }
                })
        }

        fun slideOut(view: View) {
            view.animate()
                .translationY(view.height.toFloat())
                .alpha(0.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
        }

        @Throws(IOException::class)
        fun serialize(`object`: Any): ByteArray {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream =
                ObjectOutputStream(byteArrayOutputStream)
            // transform object to stream and then to a byte array
            objectOutputStream.writeObject(`object`)
            objectOutputStream.flush()
            objectOutputStream.close()
            return byteArrayOutputStream.toByteArray()
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        fun deserialize(bytes: ByteArray): Any {
            val byteArrayInputStream = ByteArrayInputStream(bytes)
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            return objectInputStream.readObject()
        }
    }

}
