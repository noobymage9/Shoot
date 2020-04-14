package com.shoot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CustomiseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customise, container, false)
    }

    companion object {
        val KEY = "customise_fragment"

        @JvmStatic
        fun newInstance() = CustomiseFragment()
    }
}
