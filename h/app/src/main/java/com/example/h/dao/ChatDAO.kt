package com.example.h.dao

import com.example.h.data.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

    fun addChat(chat : Chat) {
        dbRef.child(chat.chatID).setValue(chat)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    fun getChat(initiatorUserID : String, receiverUserID : String, callback : (chat : Chat?, errorMessage: String?) -> Unit) {

        dbRef.orderByChild("InitiatorUserID").equalTo(initiatorUserID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (chatSnapshot in snapshot.children) {
                            val userID = chatSnapshot.child("ReceiverUserID").getValue(String::class.java)
                            if (userID == receiverUserID) {
                                val chat = chatSnapshot.getValue(Chat::class.java)
                                callback(chat, null)
                                return
                            }
                        }
                    }

                    callback(null, "Chat not found")
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, "Error: $error")
                }
            })
    }
}