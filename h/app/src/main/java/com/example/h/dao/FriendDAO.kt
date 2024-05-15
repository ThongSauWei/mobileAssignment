package com.example.h.dao

import com.example.h.data.Friend
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Friend")

    private var nextID = 100

    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    suspend fun addFriend(friend : Friend) {
        if (getFriend(friend.requestUserID, friend.receiveUserID) != null) {

            friend.friendID = "F$nextID"
            nextID++

            dbRef.child(friend.friendID).setValue(friend)
                .addOnCompleteListener{

                }
                .addOnFailureListener{

                }.await()
        }
    }

    suspend fun getFriendList(userID : String) : List<Friend> = withContext(Dispatchers.IO) {

        val deferred = CompletableDeferred<List<Friend>>()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList = ArrayList<Friend>()
                if (snapshot.exists()) {
                    for (friendSnapshot in snapshot.children) {
                        val status = friendSnapshot.child("status").getValue(String::class.java)
                        if (status == "Friend") {
                            val friend = friendSnapshot.getValue(Friend::class.java)
                            friendList.add(friend!!)
                        }
                    }

                    val iterator = friendList.iterator()
                    while (iterator.hasNext()) {
                        val friend = iterator.next()
                        if (friend.requestUserID != userID && friend.receiveUserID != userID) {
                            iterator.remove()
                        }
                    }
                }
                deferred.complete(friendList)
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }

        })

        val friendList = deferred.await()

        friendList
    }

    fun updateFriend(friend : Friend) {
        dbRef.child(friend.friendID).setValue(friend)
            .addOnCompleteListener{

            }
            .addOnFailureListener{

            }
    }

    fun deleteFriend(friendID : String) {
        dbRef.child(friendID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getFriend(userID_1 : String, userID_2 : String) : Friend? {
        var friend : Friend? = null

        val snapshot = dbRef.get().await()

        if (snapshot.exists()) {
            for (friendSnapshot in snapshot.children) {
                val requestUserID = friendSnapshot.child("requestUserID").getValue(String::class.java)
                val receiveUserID = friendSnapshot.child("receiveUserID").getValue(String::class.java)
                if ((requestUserID == userID_1 && receiveUserID == userID_2) ||
                    (requestUserID == userID_2 && receiveUserID == userID_1)) {
                    friend = friendSnapshot.getValue(Friend::class.java)
                    break
                }
            }
        }

        return friend
    }

    private suspend fun getNextID() : Int {
        var friendID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (friendSnapshot in snapshot.children) {
                val lastFriendID = friendSnapshot.key!!
                friendID = lastFriendID.substring(1).toInt() + 1
            }
        }

        return friendID
    }
}