package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class AboutUs : Fragment() {

    private lateinit var btnExitAboutUs: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        (activity as MainActivity).setToolbar()

        btnExitAboutUs = view.findViewById(R.id.btnExitAboutUs)

        btnExitAboutUs.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        return view
    }

}