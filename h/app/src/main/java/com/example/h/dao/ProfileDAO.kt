package com.example.h.dao

import android.app.Application
import com.example.h.data.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProfileDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Profile")

    fun addProfile(profile : Profile) {
        dbRef.child(profile.userID).setValue(profile)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getProfile(userID : String) : Profile? = suspendCoroutine { continuation ->

        dbRef.orderByChild("userID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (profileSnapshot in snapshot.children) {
                            val profile = profileSnapshot.getValue(Profile::class.java)
                            continuation.resume(profile)
                            return
                        }
                    }
                    continuation.resume(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })
    }
}