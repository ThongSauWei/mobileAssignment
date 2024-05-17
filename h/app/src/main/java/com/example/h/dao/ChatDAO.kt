package com.example.h.dao

import com.example.h.data.Chat
import com.example.h.data.Friend
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

class ChatDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

    private var nextID = 100

    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    fun addChat(chat : Chat) {
        chat.chatID = "C$nextID"
        nextID++

        dbRef.child(chat.chatID).setValue(chat)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    suspend fun getChat(userID_1 : String, userID_2 : String) : Chat? {
        var chat : Chat? = null

        val snapshot = dbRef.get().await()

        if (snapshot.exists()) {
            for (chatSnapshot in snapshot.children) {
                val initiatorUserID = chatSnapshot.child("initiatorUserID").getValue(String::class.java)
                val receiverUserID = chatSnapshot.child("receiverUserID").getValue(String::class.java)
                if ((initiatorUserID == userID_1 && receiverUserID == userID_2) ||
                    (initiatorUserID == userID_2 && receiverUserID == userID_1)) {
                    chat = chatSnapshot.getValue(Chat::class.java)
                    break
                }
            }
        }

        return chat
    }

    suspend fun getChatByID(chatID : String) : Chat? = suspendCoroutine { continuation ->

        dbRef.orderByChild("chatID").equalTo(chatID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (chatSnapshot in snapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            continuation.resume(chat)
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

    suspend fun getChatByUser(userID : String) : List<Chat> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = ArrayList<Chat>()
                    if (snapshot.exists()) {
                        for (chatSnapshot in snapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            chatList.add(chat!!)
                        }

                        val iterator = chatList.iterator()
                        while (iterator.hasNext()) {
                            val chat = iterator.next()
                            if (chat.initiatorUserID != userID && chat.receiverUserID != userID) {
                                iterator.remove()
                            }
                        }

                        continuation.resume(chatList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    fun deleteChat(chatID : String) {
        dbRef.child(chatID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private suspend fun getNextID() : Int {
        var chatID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (chatSnapshot in snapshot.children) {
                val lastChatID = chatSnapshot.key!!
                chatID = lastChatID.substring(1).toInt() + 1
            }
        }

        return chatID
    }
}