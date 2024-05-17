package com.example.h.dao

import android.app.Application
import com.example.h.data.Group
import com.example.h.data.User
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

class GroupDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Group")

    private var nextID = 100

    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    fun addGroup(group : Group) {
        group.groupID = "G$nextID"
        nextID++

        dbRef.child(group.groupID).setValue(group)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getGroup(groupID : String) : Group? = suspendCoroutine { continuation ->

        dbRef.orderByChild("groupID").equalTo(groupID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (groupSnapshot in snapshot.children) {
                            val group = groupSnapshot.getValue(Group::class.java)
                            continuation.resume(group)
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

    suspend fun searchGroup(searchText : String) : List<Group> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            val groupList = ArrayList<Group>()

            dbRef.orderByChild("groupName")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (groupSnapshot in snapshot.children) {
                                val groupName =
                                    groupSnapshot.child("groupName").getValue(String::class.java)!!
                                if (groupName.startsWith(searchText, true)) {
                                    val group = groupSnapshot.getValue(Group::class.java)
                                    groupList.add(group!!)
                                }
                            }
                        }

                        continuation.resume(groupList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }

    fun deleteGroup(groupID : String) {
        dbRef.child(groupID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private suspend fun getNextID() : Int {
        var groupID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (groupSnapshot in snapshot.children) {
                val lastGroupID = groupSnapshot.key!!
                groupID = lastGroupID.substring(1).toInt() + 1
            }
        }

        return groupID
    }
}