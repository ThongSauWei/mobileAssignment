package com.example.h.dao

import android.app.Application
import com.example.h.data.GroupChatLine
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

class GroupChatLineDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("GroupChatLine")
    private var groupChatLineList = ArrayList<GroupChatLine>()

    fun addGroupChatLine(groupChatLine : GroupChatLine) {
        dbRef.child(groupChatLine.groupChatLineID).setValue(groupChatLine)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getGroupChatLine(groupID : String) : List<GroupChatLine> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("groupID").equalTo(groupID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        groupChatLineList.clear()
                        if (snapshot.exists()) {
                            for (groupChatLineSnapshot in snapshot.children) {
                                val groupChatLine = groupChatLineSnapshot.getValue(GroupChatLine::class.java)
                                groupChatLineList.add(groupChatLine!!)
                            }
                        }

                        continuation.resume(groupChatLineList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }
}