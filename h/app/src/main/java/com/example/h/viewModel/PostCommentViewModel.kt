package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.PostCommentDAO
import com.example.h.data.PostComment
import com.example.h.repository.PostCommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostCommentViewModel(application : Application) : AndroidViewModel(application) {
    val postCommentRepository : PostCommentRepository

    init {
        val postCommentDao = PostCommentDAO()
        postCommentRepository = PostCommentRepository(postCommentDao)
    }

    fun addPostComment(postComment : PostComment) {
        viewModelScope.launch(Dispatchers.IO) {
            postCommentRepository.addPostComment(postComment)
        }
    }

    suspend fun getPostComment(postID : String) : List<PostComment> {
        return postCommentRepository.getPostComment(postID)
    }

    fun deletePostComment(postCommentID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            postCommentRepository.deletePostComment(postCommentID)
        }
    }
}