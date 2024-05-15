package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.Profile
import com.example.h.R
import com.example.h.dao.ProfileDAO
import com.example.h.dao.UserDAO
import com.example.h.data.PostComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentAdapter(private var comments: List<PostComment>, private val coroutineScope: CoroutineScope) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    private val userDAO = UserDAO()
    private val profileDAO = ProfileDAO()

    class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCommentCommentHolder: TextView = itemView.findViewById(R.id.tvCommentCommentHolder)
        val imgProfileCommentHolder: ImageView = itemView.findViewById(R.id.imgProfileCommentHolder)
    }

    fun updateComments(newComments: List<PostComment>) {
        comments = newComments
        notifyDataSetChanged() // Notify the RecyclerView that the data set has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_holder, parent, false)
        return CommentHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val comment = comments[position]
        coroutineScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDAO.getUserByID(comment.userID)
            }
            val prof = withContext(Dispatchers.IO) {
                profileDAO.getProfile(comment.userID)
            }
            holder.tvCommentCommentHolder.text = comment.content
            prof?.let {
                Glide.with(holder.itemView.context)
                    .load(it.userImage)
                    .into(holder.imgProfileCommentHolder)
            }
        }
    }


}
