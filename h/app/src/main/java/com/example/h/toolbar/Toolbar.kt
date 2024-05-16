package com.example.h.toolbar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.MainActivity
import com.example.h.Profile
import com.example.h.R
import com.example.h.dataAdapter.GroupAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Toolbar : Fragment() {
    private lateinit var toolbar : Toolbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.toolbar_with_profile, container, false)

        // Load user ID from SharedPreferences
        val userID = SaveSharedPreference.getUserID(requireContext())


        // Retrieve the user image URL from Firebase using the user ID
        val dbRef = FirebaseDatabase.getInstance().getReference("User").child(userID)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userImageUrl = snapshot.child("userImageUrl").getValue(String::class.java)
                val profileImageView = view.findViewById<ImageView>(R.id.imgProfileToolbarWithProfile)

                // Set the profile image using Glide only if URL is not null
                if (userImageUrl != null && userImageUrl.isNotEmpty()) {
                    Glide.with(this@Toolbar)
                        .load(userImageUrl)
                        .into(profileImageView)
                }

                // Set OnClickListener on profileImageView
                profileImageView.setOnClickListener {
                    // Navigate to the Profile fragment
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    val fragment = Profile()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show()
                Log.e("Toolbar", "Database error: ${error.message}", error.toException())
            }
        })

        toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }

        return view
    }
}