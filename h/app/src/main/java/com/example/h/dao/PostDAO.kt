package com.example.h.dao

import android.app.Application
import android.net.Uri
import com.example.h.data.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PostDAO {
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
    private var postList = ArrayList<Post>()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("postImages")

//    fun addPost(post : Post) {
//        dbRef.child(post.postID).setValue(post)
//            .addOnCompleteListener {
//
//            }
//            .addOnFailureListener {
//
//            }
//    }

    suspend fun addPost(post: Post, imageUri: Uri?, userID: String, onComplete: (Boolean, Exception?) -> Unit) {
        // Retrieve the last post ID from Firebase to generate the next post ID
        val lastPostID = getLastPostID(userID)
        val nextPostID = generateNextPostID(lastPostID)

        if (imageUri != null) {
            val imageRef = storageRef.child("$nextPostID.png")
            imageRef.putFile(imageUri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageRef.downloadUrl
                            .addOnCompleteListener { urlTask ->
                                if (urlTask.isSuccessful) {
                                    val imageUrl = urlTask.result.toString()
                                    val updatedPost = post.copy(postID = nextPostID, postImage = imageUrl)
                                    savePostToDatabase(updatedPost, onComplete)
                                } else {
                                    onComplete(false, urlTask.exception)
                                }
                            }
                    } else {
                        onComplete(false, task.exception)
                    }
                }
        } else {
            val updatedPost = post.copy(postID = nextPostID)
            savePostToDatabase(updatedPost, onComplete)
        }
    }

    private suspend fun getLastPostID(userID: String): String? = suspendCoroutine { continuation ->
        dbRef.orderByChild("userID").equalTo(userID).limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lastPostID = snapshot.children.first().key
                        continuation.resume(lastPostID)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
    }

    private fun generateNextPostID(lastPostID: String?): String {
        val lastPostNumber = lastPostID?.substring(1)?.toIntOrNull() ?: 0
        val nextPostNumber = lastPostNumber + 1
        return "P${nextPostNumber.toString().padStart(3, '0')}"
    }

    private fun savePostToDatabase(post: Post, onComplete: (Boolean, Exception?) -> Unit) {
        dbRef.child(post.postID).setValue(post)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
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