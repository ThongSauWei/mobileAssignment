package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class SignIn : Fragment() {

    private lateinit var txtEmailSignIn: EditText
    private lateinit var txtPasswordSignIn: EditText
    private lateinit var btnSignInSignIn: Button
    private lateinit var btnExitSignIn: ImageView
    private lateinit var btnRegisterSignIn: Button
    private lateinit var btnForgetSignIn: Button
    private lateinit var imgBtnGoogleSignIn: ImageButton
    private lateinit var btnFacebookSignIn: Button

    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        txtEmailSignIn = view.findViewById(R.id.txtEmailSignIn)
        txtPasswordSignIn = view.findViewById(R.id.txtPasswordSignIn)
        btnSignInSignIn = view.findViewById(R.id.btnSignInSignIn)
        btnExitSignIn = view.findViewById(R.id.btnExitSignIn)
        btnRegisterSignIn = view.findViewById(R.id.btnRegisterSignIn)
        btnForgetSignIn = view.findViewById(R.id.btnForgetSignIn)
        imgBtnGoogleSignIn = view.findViewById(R.id.imgBtnGoogleSignIn)
        btnFacebookSignIn = view.findViewById(R.id.btnFacebookSignIn)

        mAuth = FirebaseAuth.getInstance()

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

        btnForgetSignIn.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = PasswordRecovery()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }
}