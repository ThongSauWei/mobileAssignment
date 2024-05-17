package com.example.h.dao

import com.example.h.data.Chat
import com.example.h.data.UserGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserGroupDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("UserGroup")
    private var userGroupList = ArrayList<UserGroup>()

    private var nextID = 100

    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    fun addUserGroup(userGroup : UserGroup) {
        userGroup.userGroupID = "UG$nextID"
        nextID++

        dbRef.child(userGroup.userGroupID).setValue(userGroup)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getUserGroup(userID : String, groupID : String) : UserGroup? = suspendCoroutine { continuation ->

        dbRef.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userGroupSnapshot in snapshot.children) {
                        val getUserID = userGroupSnapshot.child("userID").getValue(String::class.java)
                        val getGroupID = userGroupSnapshot.child("groupID").getValue(String::class.java)
                        if (userID == getUserID && groupID == getGroupID) {
                            val userGroup = userGroupSnapshot.getValue(UserGroup::class.java)
                            continuation.resume(userGroup!!)
                            return
                        }

                        continuation.resume(null)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }

        })
    }

    fun updateLastSeen(userGroup : UserGroup) {
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

    fun deleteUserGroup(userGroupID : String) {
        dbRef.child(userGroupID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private suspend fun getNextID() : Int {
        var userGroupID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (userGroupSnapshot in snapshot.children) {
                val lastUserGroupID = userGroupSnapshot.key!!
                userGroupID = lastUserGroupID.substring(2).toInt() + 1
            }
        }

        return userGroupID
    }
}