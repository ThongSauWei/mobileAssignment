package com.example.h.dataAdapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.JoinGroup
import com.example.h.R
import com.example.h.dao.UserDAO
import com.example.h.data.Post
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(var postList: List<Post>) : RecyclerView.Adapter <PostAdapter.PostHolder>() {

    private lateinit var userViewModel : UserViewModel
    //private val userDAO = UserDAO()
    private lateinit var storageRef : StorageReference
    object Constants {
        const val IMAGE_WIDTH_DP = 258
        const val IMAGE_HEIGHT_DP = 214
    }

    class PostHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfilePostHolder)
        val tvName: TextView = itemView.findViewById(R.id.tvNamePostHolder)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTimePostHolder)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPostPostHolder)
        val tvPostTitle: TextView = itemView.findViewById(R.id.tvPostTitlePostHolder)
        val tvPostContent: TextView = itemView.findViewById(R.id.tvPostContentPostHolder)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategoryPostHolder)
        val tvCategory2: TextView = itemView.findViewById(R.id.tvCategory2PostHolder)

    }

    fun setViewModel(userViewModel : UserViewModel) {
        this.userViewModel = userViewModel

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_holder, parent, false)

        storageRef = FirebaseStorage.getInstance().getReference()

        return PostHolder(itemView)
    }

    override fun getItemCount(): Int {
        return postList.size
//        return 4
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        val currentItem = postList[position]
        // Bind data to views
        holder.tvName.text = currentItem.postTitle
        holder.tvDateTime.text = currentItem.postDateTime
        holder.tvPostTitle.text = currentItem.postTitle
        holder.tvPostContent.text = currentItem.postDescription
        holder.tvCategory.text = currentItem.postCategory
        holder.tvCategory2.text = currentItem.postLearningStyle

        // Format the postDateTime
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("d/M/yyyy hh:mma", Locale.getDefault())
        val date = inputFormat.parse(currentItem.postDateTime)
        val formattedDate = outputFormat.format(date)
        holder.tvDateTime.text = formattedDate

        // Set layout params for imgPost ImageView
        val layoutParams = holder.imgPost.layoutParams
        layoutParams.width = dpToPx(holder.itemView.context, Constants.IMAGE_WIDTH_DP)
        layoutParams.height = dpToPx(holder.itemView.context, Constants.IMAGE_HEIGHT_DP)
        holder.imgPost.layoutParams = layoutParams

        // Load post image to the imgPost
        Picasso.get()
            .load(currentItem.postImage)
            .into(holder.imgPost)

        //fecth user get the username
        GlobalScope.launch(Dispatchers.Main) {
            val user = userViewModel.getUserByID(currentItem.userID)
            holder.tvName.text = user?.username ?: "Unknown"

            user?.let {
                val ref = storageRef.child("imageProfile").child("${user?.userID}.png")
                ref.downloadUrl
                    .addOnCompleteListener {
                        Glide.with(holder.imgProfile).load(it.result.toString()).into(holder.imgProfile)
                    }
            }
        }




        // Set onClickListener for each item in the RecyclerView
        holder.itemView.setOnClickListener {
            val postID = currentItem.postID
            val fragment = JoinGroup.newInstance(postID)
            val transaction = (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

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

    private fun dpToPx(context: Context, dp: Int): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
        Log.d("PostAdapter", "Converted ${dp}dp to ${px}px")
        return px
    }

    //go to JoinGroup page
    private fun joinGroup(context: Context) {
        val fragment = JoinGroup()
        val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}