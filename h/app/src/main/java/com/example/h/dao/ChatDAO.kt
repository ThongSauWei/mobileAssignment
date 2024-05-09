package com.example.h.dao

import com.example.h.data.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

    fun addChat(chat : Chat) {
        dbRef.child(chat.chatID).setValue(chat)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    suspend fun getChat(initiatorUserID : String, receiverUserID : String) : Chat? = suspendCoroutine { continuation ->

        dbRef.orderByChild("initiatorUserID").equalTo(initiatorUserID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (chatSnapshot in snapshot.children) {
                            val userID = chatSnapshot.child("receiverUserID").getValue(String::class.java)
                            if (userID == receiverUserID) {
                                val chat = chatSnapshot.getValue(Chat::class.java)
                                continuation.resume(chat)
                                return
                            }
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