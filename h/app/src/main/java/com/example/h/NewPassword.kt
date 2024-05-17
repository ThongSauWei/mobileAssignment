package com.example.h

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class NewPassword : Fragment() {
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var backBtnNewPass: ImageView
    private lateinit var submitNewPass: AppCompatButton
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_password, container, false)
        (activity as MainActivity).setToolbar()

        newPassword = view.findViewById(R.id.txtNewPasswordNewPassword)
        confirmPassword = view.findViewById(R.id.txtConfirmPasswordNewPassword)
        backBtnNewPass = view.findViewById(R.id.btnExitNewPassword)
        submitNewPass = view.findViewById(R.id.btnSubmitNewPassword)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        backBtnNewPass.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        submitNewPass.setOnClickListener{
            val newPass = newPassword.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (newPass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (newPass == confirmPass) {
                    if (isValidPassword(newPass)) {
                        val userID = SaveSharedPreference.getUserID(requireContext())
                        userViewModel.viewModelScope.launch {
                            // Hash the password using UserRepository's hashPassword function
                            val hashedPassword = userViewModel.hashPassword(newPass)
                            // Update the password
                            val user = userViewModel.getUserByID(userID)
                            if (user != null) {
                                user.userPassword = hashedPassword
                                userViewModel.updateUser(user)
                                Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                // Navigate back to the sign-in fragment
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                val fragment = SignIn()
                                transaction?.replace(R.id.fragmentContainerView, fragment)
                                transaction?.addToBackStack(null)
                                transaction?.commit()
                            } else {
                                Toast.makeText(requireContext(), "Failed to update password!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        newPassword.error = "Password must be at least 8 characters long, contain 1 special character, 1 uppercase letter, and 1 lowercase letter"
                        Toast.makeText(requireContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    confirmPassword.error = "Passwords do not match!"
                    Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                newPassword.error = "Password cannot be empty!"
                confirmPassword.error = "Password cannot be empty!"
                Toast.makeText(requireContext(), "Password cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_!])(?=\\S+$).{8,}$")
        return pattern.matcher(password).matches()
    }
}