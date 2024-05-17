package com.example.h

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignIn : Fragment() {

    private lateinit var txtEmailSignIn: EditText
    private lateinit var txtPasswordSignIn: EditText
    private lateinit var btnSignInSignIn: Button
    private lateinit var btnExitSignIn: ImageView
    private lateinit var btnRegisterSignIn: Button
    private lateinit var btnForgetSignIn: Button

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        (activity as MainActivity).setToolbar()
        txtEmailSignIn = view.findViewById(R.id.txtEmailSignIn)
        txtPasswordSignIn = view.findViewById(R.id.txtPasswordSignIn)
        btnSignInSignIn = view.findViewById(R.id.btnSignInSignIn)
        btnExitSignIn = view.findViewById(R.id.btnExitSignIn)
        btnRegisterSignIn = view.findViewById(R.id.btnRegisterSignIn)
        btnForgetSignIn = view.findViewById(R.id.btnForgetSignIn)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        btnExitSignIn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        btnRegisterSignIn.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Register()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnSignInSignIn.setOnClickListener {
            val email = txtEmailSignIn.text.toString()
            val password = txtPasswordSignIn.text.toString()

            if (validateInput(email, password)) {
                signInWithEmailAndPassword(email, password)
            }
        }

        btnForgetSignIn.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = PasswordRecovery()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val user = userViewModel.getUserByLogin(email, password)
                if (user != null) {
                    // Save user ID to shared preferences
                    SaveSharedPreference.setUserID(requireContext(), user.userID)

                    val transaction = activity?.supportFragmentManager?.beginTransaction()//after success go to home
                    val fragment = Home()
                    transaction?.replace(R.id.fragmentContainerView, fragment)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                    Toast.makeText(requireContext(), "Welcome to FPF!", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message to the user.
                    Toast.makeText(
                        requireContext(), "Authentication failed. Invalid Email/Password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Handle any exceptions that may occur during sign-in
                Toast.makeText(
                    requireContext(), "Authentication failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}