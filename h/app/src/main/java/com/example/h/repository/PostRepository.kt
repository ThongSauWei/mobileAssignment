package com.example.h.repository

import android.net.Uri
import com.example.h.dao.PostDAO
import com.example.h.data.Post

class PostRepository(private val postDao : PostDAO) {

//    fun addPost(post : Post) {
//        postDao.addPost(post)
//    }

    suspend fun addPost(post: Post, imageUri: Uri?, userID: String, onComplete: (Boolean, Exception?) -> Unit) {
        postDao.addPost(post, imageUri, userID, onComplete)
    }


    suspend fun getPostByUser(userID : String) : List<Post> {
        return postDao.getPostByUser(userID)
    }

    suspend fun getPostByCategory(postCategory : String) : List<Post> {
        return postDao.getPostByCategory(postCategory)
    }

    suspend fun getPostByLearningStyle(postLearningStyle : String) : List<Post> {
        return postDao.getPostByLearningStyle(postLearningStyle)
    }

    suspend fun getAllPost() : List<Post> {
        return postDao.getAllPost()
    }

    fun deletePost(postID : String) {
        postDao.deletePost(postID)
    }
}