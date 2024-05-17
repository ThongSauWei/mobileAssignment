package com.example.h

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.dao.UserDAO
import com.example.h.data.PostComment
import com.example.h.dataAdapter.CommentAdapter
import com.example.h.dataAdapter.PostAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.PostCommentViewModel
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class JoinGroup : Fragment() {

    private lateinit var imgProfileJoinGroup: ImageView
    private lateinit var btnSendComment: ImageView
    private lateinit var btnJoinJoinGroup: AppCompatButton
    private lateinit var btnBackJoinGroup: ImageView
    private lateinit var recyclerViewCommentJoinGroup: RecyclerView
    private lateinit var txtCommentJoinGroup: EditText
    private lateinit var tvCommentCountJoinGroup: TextView
    private lateinit var postID: String
    private lateinit var postViewModel: PostViewModel
    private lateinit var userViewModel : UserViewModel
    //private val userDAO = UserDAO()
    private lateinit var storageRef : StorageReference

    companion object {
        private const val ARG_POST_ID = "postID"

        fun newInstance(postID: String): JoinGroup {
            val fragment = JoinGroup()
            val args = Bundle()
            args.putString(ARG_POST_ID, postID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_join_group, container, false)

        (activity as MainActivity).setToolbar()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        storageRef = FirebaseStorage.getInstance().getReference()
        // Get the postID from arguments
        postID = arguments?.getString(ARG_POST_ID) ?: ""

        // Initialize views
        imgProfileJoinGroup = view.findViewById(R.id.imgProfileJoinGroup)
        btnSendComment = view.findViewById(R.id.btnSendComment)
        btnJoinJoinGroup = view.findViewById(R.id.btnJoinJoinGroup)
        btnBackJoinGroup = view.findViewById(R.id.btnBackJoinGroup)
        recyclerViewCommentJoinGroup = view.findViewById(R.id.recyclerViewCommentJoinGroup)
        txtCommentJoinGroup = view.findViewById(R.id.txtCommentJoinGroup)
        tvCommentCountJoinGroup = view.findViewById(R.id.tvCommentCountJoinGroup)

        // Initialize PostViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Set click listener for the back button
        btnBackJoinGroup.setOnClickListener {
            activity?.onBackPressed()
        }

        // Set up RecyclerView
        recyclerViewCommentJoinGroup.layoutManager = LinearLayoutManager(activity)
        recyclerViewCommentJoinGroup.adapter = CommentAdapter(emptyList(), viewLifecycleOwner.lifecycleScope)

        Log.d("postid", postID)
        // Fetch and display post details

        fetchPostDetails(postID)

        btnJoinJoinGroup.setOnClickListener {
            val fragment = Groups()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnSendComment.setOnClickListener {
            val commentContent = txtCommentJoinGroup.text.toString()
            if (commentContent.isNotEmpty()) {
                // Create a PostComment object with the necessary data
                val postComment = PostComment(
                    postCommentID = "", // This will be autogenerated by Firebase
                    postID = postID,
                    userID = getCurrentUserID(),
                    dateTime = getCurrentDateTime(),
                    content = commentContent
                )

                // Call the function to add the comment to Firebase
                addCommentToFirebase(postComment)

                // Clear the comment text field after sending the comment
                txtCommentJoinGroup.text.clear()

                val fragment = JoinGroup.newInstance(postID)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragmentContainerView, fragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }

        return view
    }

    private fun fetchPostDetails(postID: String) {
        lifecycleScope.launch {
            try {
                // Fetch post details using postID
                val post = postViewModel.getPostByID(postID)

                // Update UI with the retrieved post data
                post?.let { // Check if post is not null
                    // Update UI elements with post details


                    // Set name if available
                    val tvNameJoinGroup = view?.findViewById<TextView>(R.id.tvNameJoinGroup)
                    val user = userViewModel.getUserByID(post.userID)
                    tvNameJoinGroup?.text = user?.username ?: "Unknown"

                    // Set profile image if available
                    val imgProfileJoinGroup = view?.findViewById<ImageView>(R.id.imgProfileJoinGroup)
                    //post.profileImage?.let { imgProfileJoinGroup?.setImageResource(it) }
                    val ref = storageRef.child("imageProfile").child("${user?.userID}.png")
                    ref.downloadUrl
                        .addOnCompleteListener {
                            if (imgProfileJoinGroup != null) {
                                Glide.with(imgProfileJoinGroup).load(it.result.toString()).into(imgProfileJoinGroup)
                            }
                        }

                    // Set date and time if available
                    val tvDateTimeJoinGroup = view?.findViewById<TextView>(R.id.tvDateTimeJoinGroup)
                    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                    val outputFormat = SimpleDateFormat("d/M/yyyy hh:mma", Locale.getDefault())
                    val date = inputFormat.parse(post.postDateTime)
                    val formattedDate = outputFormat.format(date)
                    tvDateTimeJoinGroup?.text = formattedDate

                    // Set post image if available
                    val imgPostJoinGroup = view?.findViewById<ImageView>(R.id.imgPostJoinGroup)
                    if (imgPostJoinGroup != null) {
                        val layoutParams = imgPostJoinGroup.layoutParams
                        layoutParams.width = dpToPx(requireContext(), PostAdapter.Constants.IMAGE_WIDTH_DP)
                        layoutParams.height = dpToPx(requireContext(), PostAdapter.Constants.IMAGE_HEIGHT_DP)
                        imgPostJoinGroup.layoutParams = layoutParams

                        Glide.with(requireContext())
                            .load(post.postImage) // Assuming postImage is a URL
                            .into(imgPostJoinGroup)
                    }

                    // Set post title if available
                    val tvPostTitleJoinGroup = view?.findViewById<TextView>(R.id.tvPostTitleJoinGroup)
                    tvPostTitleJoinGroup?.text = post.postTitle
                    Log.d("posttitle",post.postTitle)
                    // Set post content if available
                    val tvPostContentJoinGroup = view?.findViewById<TextView>(R.id.tvPostContentJoinGroup)
                    tvPostContentJoinGroup?.text = post.postDescription

                    // Set category if available
                    val tvCategoryJoinGroup = view?.findViewById<TextView>(R.id.tvCategoryJoinGroup)
                    tvCategoryJoinGroup?.text = post.postCategory

                    // Set category 2 if available
                    val tvCategory2JoinGroup = view?.findViewById<TextView>(R.id.tvCategory2JoinGroup)
                    tvCategory2JoinGroup?.text = post.postLearningStyle

                    // Observe changes in comment count and update UI accordingly
                    observeAndRefreshComments(postID)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Handle error
                }
            }
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun addCommentToFirebase(postComment: PostComment) {
        val postCommentViewModel = ViewModelProvider(this).get(PostCommentViewModel::class.java)
        postCommentViewModel.addPostComment(postComment)
        // Observe changes in comment addition
        postCommentViewModel.addCommentSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                // Comment added successfully, fetch updated comments
                observeAndRefreshComments(postID)
            } else {
                // Comment failed to add, display a Toast indicating failure
                Toast.makeText(requireContext(), "Failed to add comment. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("d/M/yyyy hh:mma", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getCurrentUserID(): String {
        return SaveSharedPreference.getUserID(requireContext())
    }

    private fun observeAndRefreshComments(postID: String) {
        lifecycleScope.launch {
            try {
                // Fetch and observe comments for the specified post ID
                val postCommentViewModel = ViewModelProvider(this@JoinGroup).get(PostCommentViewModel::class.java)
                val userViewModel : UserViewModel =
                    ViewModelProvider(this@JoinGroup).get(UserViewModel::class.java)
                postCommentViewModel.getPostComment(postID)
                postCommentViewModel.postComments.observe(viewLifecycleOwner, Observer { comments ->
                    // Update the RecyclerView adapter with the new list of comments
                    val adapter = recyclerViewCommentJoinGroup.adapter as CommentAdapter
                    adapter.setViewModel(userViewModel)
                    adapter.updateComments(comments)

                    // Update the comment count TextView with the total count of comments
                    tvCommentCountJoinGroup.text = "${comments.size} comments"
                })
            } catch (e: Exception) {
                // Handle the exception
                Log.e("JoinGroup", "Error observing comment count: ${e.message}")
            }
        }
    }
}
