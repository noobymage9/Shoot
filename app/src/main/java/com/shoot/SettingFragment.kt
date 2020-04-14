package com.shoot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {

    lateinit var root : View
    val save = Runnable {
        val db = context?.let { context -> DB.newInstance(context) }
        db?.userDao()?.insertUser(User(0, root.name_field.text.toString()))
        root.name_field.text.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_setting, container, false)
        root.save.setOnClickListener(View.OnClickListener {
            Thread(save).start()
        })
        return root
    }

    companion object {
        val KEY = "setting_fragment"

        fun newInstance() = SettingFragment()
    }
}
