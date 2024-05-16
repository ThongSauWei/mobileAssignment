package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.h.dao.PostCommentDAO
import com.example.h.data.PostComment
import com.example.h.repository.PostCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostCommentViewModel(application : Application) : AndroidViewModel(application) {
    val postCommentRepository : PostCommentRepository
    val addCommentSuccess = MutableLiveData<Boolean>()

    private val _postComments = MutableLiveData<List<PostComment>>()
    val postComments: LiveData<List<PostComment>> = _postComments

    init {
        val postCommentDao = PostCommentDAO()
        postCommentRepository = PostCommentRepository(postCommentDao)
    }

//    fun addPostComment(postComment : PostComment) {
//        viewModelScope.launch(Dispatchers.IO) {
//            postCommentRepository.addPostComment(postComment)
//        }
//    }
fun addPostComment(postComment : PostComment) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            postCommentRepository.addPostComment(postComment)
            addCommentSuccess.postValue(true) // Set LiveData value to true if added successfully
        } catch (e: Exception) {
            addCommentSuccess.postValue(false) // Set LiveData value to false if failed to add
        }
    }
}

//    suspend fun getPostComment(postID : String) : List<PostComment> {
//        return postCommentRepository.getPostComment(postID)
//    }

    suspend fun getPostComment(postID: String) {
        try {
            val comments = postCommentRepository.getPostComment(postID)
            _postComments.postValue(comments)
        } catch (e: Exception) {
            // Handle the exception
        }
    }

    fun deletePostComment(postCommentID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            postCommentRepository.deletePostComment(postCommentID)
        }
    }
}