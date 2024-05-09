package com.example.h.dao

import android.app.Application
import com.example.h.data.PostComment
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

class PostCommentDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("PostComment")
    private var postCommentList = ArrayList<PostComment>()

    fun addPostComment(postComment : PostComment) {
        dbRef.child(postComment.postCommentID).setValue(postComment)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getPostComment(postID : String) : List<PostComment> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("postID").equalTo(postID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postCommentList.clear()
                        if (snapshot.exists()) {
                            for (postCommentSnapshot in snapshot.children) {
                                val postComment = postCommentSnapshot.getValue(PostComment::class.java)
                                postCommentList.add(postComment!!)
                            }
                        }

                        continuation.resume(postCommentList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

}