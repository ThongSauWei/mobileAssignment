package com.example.h.dao

import android.app.Application
import com.example.h.data.ChatLine
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

class ChatLineDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("ChatLine")
    private var chatLineList = ArrayList<ChatLine>()

    fun addChatLine(chatLine : ChatLine) {
        dbRef.child(chatLine.chatLineID).setValue(chatLine)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getChatLine(chatID : String) : List<ChatLine> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("chatID").equalTo(chatID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatLineList.clear()
                        if (snapshot.exists()) {
                            for (chatLineSnapshot in snapshot.children) {
                                val chatLine = chatLineSnapshot.getValue(ChatLine::class.java)
                                chatLineList.add(chatLine!!)
                            }
                        }

                        continuation.resume(chatLineList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    fun deleteChatLine(chatLineID : String) {
        dbRef.child(chatLineID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }
}