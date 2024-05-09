package com.example.h.repository

import com.example.h.dao.PostCommentDAO
import com.example.h.data.PostComment

class PostCommentRepository(private val postCommentDao : PostCommentDAO) {

    fun addPostComment(postComment : PostComment) {
        postCommentDao.addPostComment(postComment)
    }

    suspend fun getPostComment(postID : String) : List<PostComment> {
        return postCommentDao.getPostComment(postID)
    }
}