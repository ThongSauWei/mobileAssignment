package com.example.h.dao

import android.app.Application
import com.example.h.data.Post
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

class PostDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
    private var postList = ArrayList<Post>()

    fun addPost(post : Post) {
        dbRef.child(post.postID).setValue(post)
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun getPostByUser(userID : String) : List<Post> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("userID").equalTo(userID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList.clear()
                        if (snapshot.exists()) {
                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(Post::class.java)
                                postList.add(post!!)
                            }
                        }

                        continuation.resume(postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    suspend fun getPostByCategory(postCategory : String) : List<Post> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("postCategory").equalTo(postCategory)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList.clear()
                        if (snapshot.exists()) {
                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(Post::class.java)
                                postList.add(post!!)
                            }
                        }

                        continuation.resume(postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    suspend fun getPostByLearningStyle(postLearningStyle : String) : List<Post> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.orderByChild("postLearningStyle").equalTo(postLearningStyle)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postList.clear()
                        if (snapshot.exists()) {
                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(Post::class.java)
                                postList.add(post!!)
                            }
                        }

                        continuation.resume(postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }

                })
        }
    }

    suspend fun getAllPost() : List<Post> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val post = postSnapshot.getValue(Post::class.java)
                            postList.add(post!!)
                        }
                    }

                    continuation.resume(postList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }

            })
        }
    }

    fun deletePost(postID : String) {
        dbRef.child(postID).removeValue()
            .addOnCompleteListener {

            }
            .addOnFailureListener {

            }
    }
}