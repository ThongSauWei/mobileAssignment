package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch

class PasswordRecovery : Fragment() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var btnContactUs: Button
    private lateinit var btnBackPR: ImageView
    private lateinit var inputEmail: EditText
    private lateinit var securityQuestion: EditText
    private lateinit var btnSubmitPassRecover: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_recovery, container, false)
        (activity as MainActivity).setToolbar()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        btnContactUs = view.findViewById(R.id.btnContactUsPasswordRecovery)
        btnBackPR = view.findViewById(R.id.btnExitPasswordRecovery)
        inputEmail = view.findViewById(R.id.txtEmailPasswordRecovery)
        securityQuestion = view.findViewById(R.id.txtSecurityQuestionPasswordRecovery)
        btnSubmitPassRecover = view.findViewById(R.id.btnSubmitPasswordRecovery)

        btnContactUs.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = ContactUs()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnBackPR.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        btnSubmitPassRecover.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            userViewModel.viewModelScope.launch {
                val user = userViewModel.getUserByLogin(email, "")
                if (user != null) {
                    val userSecurityQuestion = user.userSecurityQuestion
                    val enteredSecurityQuestion = securityQuestion.text.toString().trim()

                    if (userSecurityQuestion == enteredSecurityQuestion) {
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        val fragment = NewPassword()
                        transaction?.replace(R.id.fragmentContainerView, fragment)
                        transaction?.addToBackStack(null)
                        transaction?.commit()
                    } else {
                        securityQuestion.error = "Incorrect security question"
                        Toast.makeText(requireContext(), "Incorrect security question", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    inputEmail.error = "Email not found"
                    Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}

