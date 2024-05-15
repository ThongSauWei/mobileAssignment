package com.example.h.dao

import android.app.Application
import com.example.h.data.PostComment
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

class PostCommentDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("PostComment")
    private var postCommentList = ArrayList<PostComment>()

    private var nextID = 100

    init {
        GlobalScope.launch {
            nextID = getNextID()
        }
    }

    fun addPostComment(postComment : PostComment) {
        // Fetch the last post comment ID from Firebase
        dbRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastPostCommentSnapshot = snapshot.children.lastOrNull()
                val lastPostCommentID = lastPostCommentSnapshot?.key?.substring(2)?.toIntOrNull() ?: 0
                // Increment the nextID based on the last ID retrieved
                nextID = lastPostCommentID + 1
                // Set the postCommentID for the new comment
                postComment.postCommentID = "PC$nextID"
                // Add the post comment to Firebase
                dbRef.child(postComment.postCommentID).setValue(postComment)
                    .addOnCompleteListener {

                    }
                    .addOnFailureListener {

                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    suspend fun getPostComment(postID: String): List<PostComment> = suspendCoroutine { continuation ->
        val commentList = mutableListOf<PostComment>()
        val query = dbRef.orderByChild("postID").equalTo(postID)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(PostComment::class.java)
                    comment?.let { commentList.add(it) }
                }
                continuation.resume(commentList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                continuation.resumeWithException(databaseError.toException())
            }
        })
    }

    fun deletePostComment(postCommentID : String) {
        dbRef.child(postCommentID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    private suspend fun getNextID() : Int {
        var postCommentID = 100
        val snapshot = dbRef.orderByKey().limitToLast(1).get().await()

        if (snapshot.exists()) {
            for (postCommentSnapshot in snapshot.children) {
                val lastPostCommentID = postCommentSnapshot.key!!
                postCommentID = lastPostCommentID.substring(2).toInt() + 1
            }
        }

        return postCommentID
    }

}