package com.example.h.dao

import android.net.Uri
import com.example.h.data.Post
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PostDAO {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
    private val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("postImages")

    suspend fun addPost(post: Post, imageUri: Uri?, userID: String, onComplete: (Boolean, Exception?) -> Unit) {
        val lastPostID = getLastPostID(userID)
        val nextPostID = generateNextPostID(lastPostID)

        if (imageUri != null) {
            val imageRef = storageRef.child("$nextPostID.png")
            imageRef.putFile(imageUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageRef.downloadUrl.addOnCompleteListener { urlTask ->
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

     suspend fun getLastPostID(userID: String): String? = suspendCancellableCoroutine { continuation ->
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
        dbRef.child(post.postID).setValue(post).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception)
        }
    }

    suspend fun getPostByUser(userID: String): List<Post> = suspendCancellableCoroutine { continuation ->
        dbRef.orderByChild("userID").equalTo(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.getValue(Post::class.java)?.let { posts.add(it) }
                    }
                }
                continuation.resume(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getPostByCategory(postCategory: String): List<Post> = suspendCancellableCoroutine { continuation ->
        dbRef.orderByChild("postCategory").equalTo(postCategory).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.getValue(Post::class.java)?.let { posts.add(it) }
                    }
                }
                continuation.resume(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getPostByLearningStyle(postLearningStyle: String): List<Post> = suspendCancellableCoroutine { continuation ->
        dbRef.orderByChild("postLearningStyle").equalTo(postLearningStyle).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.getValue(Post::class.java)?.let { posts.add(it) }
                    }
                }
                continuation.resume(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getAllPost(): List<Post> = suspendCancellableCoroutine { continuation ->
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.getValue(Post::class.java)?.let { posts.add(it) }
                    }
                }
                continuation.resume(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    fun deletePost(postID: String) {
        dbRef.child(postID).removeValue().addOnCompleteListener {
            // Handle success
        }.addOnFailureListener {
            // Handle failure
        }
    }

    suspend fun getPostByID(postID: String): Post? = suspendCancellableCoroutine { continuation ->
        dbRef.child(postID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                continuation.resume(post)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getPostByCategoryAndLearningStyle(category: String, learningStyle: String): List<Post> {
        val postsByCategory = getPostByCategory(category)
        val postsByLearningStyle = getPostByLearningStyle(learningStyle)

        // Filter posts by both category and learning style
        return postsByCategory.filter { post ->
            postsByLearningStyle.any { it.postID == post.postID }
        }
    }

    suspend fun searchPost(searchText : String) : List<Post> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->

            val postList = ArrayList<Post>()

            dbRef.orderByChild("postTitle")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (postSnapshot in snapshot.children) {
                                val postTitle = postSnapshot.child("postTitle").getValue(String::class.java)!!
                                if (postTitle.startsWith(searchText, true)) {
                                    val post = postSnapshot.getValue(Post::class.java)
                                    postList.add(post!!)
                                }
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




}
