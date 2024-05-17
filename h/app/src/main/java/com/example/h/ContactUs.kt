package com.example.h

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.h.R
import com.example.h.saveSharedPreference.SaveSharedPreference

class ContactUs : Fragment() {

    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var ddlProblemType: Spinner
    private lateinit var txtDescription: EditText
    private lateinit var btnSubmit: AppCompatButton
    private lateinit var exitContactUs: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        (activity as MainActivity).setToolbar()
        txtName = view.findViewById(R.id.txtNameContactUs)
        txtEmail = view.findViewById(R.id.txtEmailContactUs)
        ddlProblemType = view.findViewById(R.id.ddlProblemTypeContactUs)
        txtDescription = view.findViewById(R.id.txtDescriptionContactUs)
        btnSubmit = view.findViewById(R.id.btnSubmitContactUs)
        exitContactUs = view.findViewById(R.id.btnExitContactUs)

        // Populate spinner with problem types
        val problemTypes = resources.getStringArray(R.array.problem_type)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, problemTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ddlProblemType.adapter = adapter

        exitContactUs.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }

        btnSubmit.setOnClickListener {
            val name = txtName.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val problemType = ddlProblemType.selectedItem.toString()
            val description = txtDescription.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && description.isNotEmpty()) {
                sendEmail(name, email, problemType, description)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun sendEmail(name: String, email: String, problemType: String, description: String) {
        val subject = "Contact Form Submission from $name - $problemType"
        val body = "Name: $name\nEmail: $email\nProblem Type: $problemType\n\n$description"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("erikafung26@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }
}