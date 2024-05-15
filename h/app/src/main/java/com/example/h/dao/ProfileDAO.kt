package com.example.h.dao

import com.example.h.data.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProfileDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Profile")
    private var userIDList = ArrayList<String>()

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

    suspend fun getUserListByCourse(course : String) : List<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("userCourse").equalTo(course).limitToFirst(10)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userIDList.clear()
                        if (snapshot.exists()) {
                            for (profileSnapshot in snapshot.children) {
                                val userID = profileSnapshot.child("userID").getValue(String::class.java)
                                userIDList.add(userID!!)
                            }
                        }

                        continuation.resume(userIDList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    suspend fun getRemainingUsers(list : List<String>) : List<String> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->
            dbRef.orderByKey()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val arrayList = ArrayList(list)
                        if (snapshot.exists()) {
                            for (profileSnapshot in snapshot.children) {
                                val userID = profileSnapshot.child("userID").getValue(String::class.java)
                                if (!arrayList.contains(userID)) {
                                    arrayList.add(userID)

                                    if (arrayList.size >= 10) {
                                        break
                                    }
                                }
                            }
                        }

                        continuation.resume(arrayList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    fun deleteProfile(userID : String) {
        dbRef.child(userID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }
}