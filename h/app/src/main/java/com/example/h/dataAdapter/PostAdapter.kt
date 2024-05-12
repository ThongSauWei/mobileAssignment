package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R
import com.example.h.data.Post

class PostAdapter(var postList: List<Post>) : RecyclerView.Adapter <PostAdapter.PostHolder>() {
    class PostHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfilePostHolder)
        val tvName: TextView = itemView.findViewById(R.id.tvNamePostHolder)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTimePostHolder)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPostPostHolder)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPostTitlePostHolder)
        val tvPostContent: TextView = itemView.findViewById(R.id.tvPostContentPostHolder)
        val tvCategory1: TextView = itemView.findViewById(R.id.tvCategoryPostHolder)
        val tvCategory2: TextView = itemView.findViewById(R.id.tvCategory2PostHolder)

        /* initialise all the views that is needed to change

        val imgProfile : ImageView = itemView.findViewById(R.id.imgProfilePostHolder)
        val tvName : TextView = itemView.findViewById(R.id.tvNamePostHolder)
        val tvDateTime : TextView = itemView.findViewById(R.id.tvDateTimePostHolder)
        val imgPost : ImageView = itemView.findViewById(R.id.imgPostPostHolder)
        val tvTitle : TextView = itemView.findViewById(R.id.tvPostTitlePostHolder)
        val tvPostContent : TextView = itemView.findViewById(R.id.tvPostContentPostHolder)
        val tvCategory1 : TextView = itemView.findViewById(R.id.tvCategoryPostHolder)
        val tvCategory2 : TextView = itemView.findViewById(R.id.tvCategory2PostHolder)
        */
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_holder, parent, false)

        return PostHolder(itemView)
    }

    override fun getItemCount(): Int {
        return postList.size
//        return 4
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        val currentItem = postList[position]

        holder.tvTitle.text = currentItem.postTitle
        holder.tvPostContent.text = currentItem.postDescription
        holder.tvCategory1.text = currentItem.postCategory
        holder.tvCategory2.text = currentItem.postLearningStyle

        /* bind the views with the correct information

        val currentItem = postList[position]

        holder.imgProfile.setImageResource(currentItem.profilePic)
        holder.tvName.text = currentItem.name
        holder.tvDateTime.text = currentItem.dateTime
        holder.imgPost.setImageResource(currentItem.postPic)
        holder.tvTitle.text = currentItem.title
        holder.tvPostContent.text = currentItem.content
        holder.tvCategory1.text = currentItem.category1
        holder.tvCategory2.text = currentItem.category2
        */
    }
}