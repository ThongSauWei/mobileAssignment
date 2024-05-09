package com.example.h.repository

import com.example.h.dao.PostDAO
import com.example.h.data.Post

class PostRepository(private val postDao : PostDAO) {

    fun addPost(post : Post) {
        postDao.addPost(post)
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