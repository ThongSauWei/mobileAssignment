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
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentAdapter(private var comments: List<PostComment>, private val coroutineScope: CoroutineScope) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    private lateinit var userViewModel : UserViewModel

    private lateinit var storageRef : StorageReference

    class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCommentCommentHolder: TextView = itemView.findViewById(R.id.tvCommentCommentHolder)
        val imgProfileCommentHolder: ImageView = itemView.findViewById(R.id.imgProfileCommentHolder)
    }

    fun setViewModel(userViewModel : UserViewModel) {
        this.userViewModel = userViewModel

        notifyDataSetChanged()
    }

    fun updateComments(newComments: List<PostComment>) {
        comments = newComments
        notifyDataSetChanged() // Notify the RecyclerView that the data set has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_holder, parent, false)
        storageRef = FirebaseStorage.getInstance().getReference()
        return CommentHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val comment = comments[position]
        coroutineScope.launch {
            val user = withContext(Dispatchers.IO) {
                userViewModel.getUserByID(comment.userID)
            }

            holder.tvCommentCommentHolder.text = comment.content

            val ref = storageRef.child("imageProfile").child("${user?.userID}.png")
            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(holder.imgProfileCommentHolder).load(it.result.toString()).into(holder.imgProfileCommentHolder)
                }
        }
    }




}
