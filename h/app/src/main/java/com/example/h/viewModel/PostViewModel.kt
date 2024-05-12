package com.example.h.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.PostDAO
import com.example.h.data.Post
import com.example.h.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel(application : Application) : AndroidViewModel(application) {
    val postRepository : PostRepository

    init {
        val postDao = PostDAO()
        postRepository = PostRepository(postDao)
    }

//    fun addPost(post : Post) {
//        viewModelScope.launch(Dispatchers.IO) {
//            postRepository.addPost(post)
//        }
//    }

    fun addPost(post: Post, imageUri: Uri?, userID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.addPost(post, imageUri, userID) { success, exception ->
                if (success) {
                    // Post addition succeeded
                    // You can perform any UI-related actions here if needed
                } else {
                    exception?.printStackTrace()
                }
            }
        }
    }


    suspend fun getPostByUser(userID : String) : List<Post> {
        return postRepository.getPostByUser(userID)
    }

    suspend fun  getPostByCategory(postCategory : String) : List<Post> {
        return postRepository.getPostByCategory(postCategory)
    }

    suspend fun getPostByLearningStyle(postLearningStyle : String) : List<Post> {
        return postRepository.getPostByLearningStyle(postLearningStyle)
    }

    suspend fun getAllPost() : List<Post> {
        return postRepository.getAllPost()
    }

    fun deletePost(postID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.deletePost(postID)
        }
    }
}