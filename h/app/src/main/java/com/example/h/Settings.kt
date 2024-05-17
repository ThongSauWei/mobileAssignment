package com.example.h

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import java.util.Locale


class Settings : Fragment() {

    private lateinit var btnAboutUs: Button
    private lateinit var btnContactUs: Button
    private lateinit var ddlLanguage: Spinner
    private lateinit var btnBackSettings: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        (activity as MainActivity).setToolbar()
        btnAboutUs = view.findViewById(R.id.btnAboutUsSettings)
        btnContactUs = view.findViewById(R.id.btnContactUsSettings)
        ddlLanguage = view.findViewById(R.id.ddlLanguageSettings)
        btnBackSettings = view.findViewById(R.id.btnBackSettings)

        // Populate the spinner with language options
        val languages = resources.getStringArray(R.array.language_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ddlLanguage.adapter = adapter

        btnBackSettings.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        btnAboutUs.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = AboutUs()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnContactUs.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = ContactUs()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        // Handle the click event for the tick button
        view.findViewById<View>(R.id.btnTickSettings).setOnClickListener {
            // Get the selected language from the spinner
            val selectedLanguage = ddlLanguage.selectedItem.toString()

            // Update the app's locale configuration based on the selected language
            updateLocale(selectedLanguage)
        }

        return view
    }

    private fun updateLocale(language: String) {
        val locale = when (language) {
            "中文" -> Locale("zh")
            else -> Locale("en")
        }

        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Restart the activity to apply the language changes
        activity?.recreate()
    }
}