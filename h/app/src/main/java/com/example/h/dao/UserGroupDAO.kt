package com.example.h.dao

import com.example.h.data.UserGroup
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

class UserGroupDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("UserGroup")
    private var userGroupList = ArrayList<UserGroup>()

    fun addUserGroup(userGroup : UserGroup) {
        dbRef.child(userGroup.userGroupID).setValue(userGroup)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getUserGroupByGroup(groupID : String) : List<UserGroup> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("groupID").equalTo(groupID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userGroupList.clear()
                        if (snapshot.exists()) {
                            for (userGroupSnapshot in snapshot.children) {
                                val userGroup = userGroupSnapshot.getValue(UserGroup::class.java)
                                userGroupList.add(userGroup!!)
                            }
                        }

                        continuation.resume(userGroupList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    suspend fun getUserGroupByUser(userID : String) : List<UserGroup> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("userID").equalTo(userID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userGroupList.clear()
                        if (snapshot.exists()) {
                            for (userGroupSnapshot in snapshot.children) {
                                val userGroup = userGroupSnapshot.getValue(UserGroup::class.java)
                                userGroupList.add(userGroup!!)
                            }
                        }

                        continuation.resume(userGroupList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }
}