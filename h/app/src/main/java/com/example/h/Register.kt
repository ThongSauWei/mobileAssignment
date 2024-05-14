package com.example.h

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.h.data.User
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.regex.Pattern

class Register : Fragment() {
    private lateinit var txtNameRegister: EditText
    private lateinit var txtEmailRegister: EditText
    private lateinit var txtPhoneNoRegister: EditText
    private lateinit var txtBirthdayRegister: EditText
    private lateinit var txtPasswordRegister: EditText
    private lateinit var txtSecurityQuestionRegister: EditText
    private lateinit var btnSignUpRegister: Button
    private lateinit var btnSignInRegister: TextView
    private lateinit var btnExitRegister : ImageView

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        txtNameRegister = view.findViewById(R.id.txtNameRegister)
        txtEmailRegister = view.findViewById(R.id.txtEmailRegister)
        txtPhoneNoRegister = view.findViewById(R.id.txtPhoneNoRegister)
        txtBirthdayRegister = view.findViewById(R.id.txtBirthdayRegister)
        txtPasswordRegister = view.findViewById(R.id.txtPasswordRegister)
        txtSecurityQuestionRegister = view.findViewById(R.id.txtSecurityQuestionRegister)
        btnSignUpRegister = view.findViewById(R.id.btnSignUpRegister)
        btnSignInRegister = view.findViewById(R.id.btnSignInRegister)
        btnExitRegister = view.findViewById(R.id.btnExitRegister)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        btnSignInRegister.setOnClickListener {
            navigateToSignIn()
        }

        btnExitRegister.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        btnSignUpRegister.setOnClickListener {
            if (validateInput()) {
                val name = txtNameRegister.text.toString()
                val email = txtEmailRegister.text.toString()
                val phoneNo = txtPhoneNoRegister.text.toString()
                val birthday = txtBirthdayRegister.text.toString()
                val password = txtPasswordRegister.text.toString()
                val securityQuestion = txtSecurityQuestionRegister.text.toString()

                lifecycleScope.launch {
                    val isRegistered = userViewModel.isEmailRegistered(email)
                    if (isRegistered) {
                        Toast.makeText(
                            requireContext(),
                            "This email is already registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val user = User(
                            userID = "", // Generate or retrieve user ID as needed
                            username = name,
                            userEmail = email,
                            userMobileNo = phoneNo,
                            userDOB = birthday,
                            userPassword = password,
                            userSecurityQuestion = securityQuestion
                        )

                        userViewModel.addUser(user)

                        // After successful registration, navigate to SignIn and show a toast
                        navigateToSignIn()
                        Toast.makeText(
                            requireContext(),
                            "Registration successful! Please login.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        txtBirthdayRegister.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker()
            }
        }

        return view
    }



    private fun navigateToSignIn() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragment = SignIn()
        transaction?.replace(R.id.fragmentContainerView, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun validateInput(): Boolean {
        val name = txtNameRegister.text.toString()
        val email = txtEmailRegister.text.toString()
        val phoneNo = txtPhoneNoRegister.text.toString()
        val birthday = txtBirthdayRegister.text.toString()
        val password = txtPasswordRegister.text.toString()
        val securityQuestion = txtSecurityQuestionRegister.text.toString()

        if (name.isEmpty() || email.isEmpty() || phoneNo.isEmpty() || birthday.isEmpty() || password.isEmpty() || securityQuestion.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidPhoneNumber(phoneNo)) {
            Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!isValidPassword(password)) {
            Toast.makeText(requireContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phoneNo: String): Boolean {
        return phoneNo.matches("[0-9]+".toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_!])(?=\\S+\$).{8,}\$")
        return pattern.matcher(password).matches()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            txtBirthdayRegister.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}