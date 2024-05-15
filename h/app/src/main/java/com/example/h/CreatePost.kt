package com.example.h

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.h.data.Post
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.PostViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CreatePost : Fragment() {

    private lateinit var txtTitleCreatePost: EditText
    private lateinit var txtDescriptionCreatePost: EditText
    private lateinit var txtLinkCreatePost: EditText
    private lateinit var ddlCategoryCreatePost: Spinner
    private lateinit var ddlLearningStyleCreatePost: Spinner
    private lateinit var btnNextCreatePost: AppCompatButton
    private lateinit var btnAddCreatePost: ImageView
    private lateinit var cardView2CreatePost: CardView
    private lateinit var imageView: ImageView

    private val imagePickRequestCode = 1000
    private var imageUri: Uri? = null

    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        txtTitleCreatePost = view.findViewById(R.id.txtTitleCreatePost)
        txtDescriptionCreatePost = view.findViewById(R.id.txtDescriptionCreatePost)
        txtLinkCreatePost = view.findViewById(R.id.txtLinkCreatePost)
        ddlCategoryCreatePost = view.findViewById(R.id.ddlCategoryCreatePost)
        ddlLearningStyleCreatePost = view.findViewById(R.id.ddlLearningStyleCreatePost)
        btnNextCreatePost = view.findViewById(R.id.btnNextCreatePost)
        btnAddCreatePost = view.findViewById(R.id.btnAddCreatePost)
        cardView2CreatePost = view.findViewById(R.id.cardView2CreatePost)
        imageView = ImageView(requireContext())

        val cardViewCreatePost = view.findViewById<CardView>(R.id.cardViewCreatePost)
        cardViewCreatePost.addView(imageView)

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        btnNextCreatePost.setOnClickListener {
            if (validateForm()) {
                //Log.d("CreatePosttest", "test")
                //val post = createPostObject()
                //postViewModel.setPost(post)

                //Log.d("CreatePost1", "post: $post")

                //save post data
                savePostToFirebase()

                // Observe the post creation status
                postViewModel.postCreationStatus.observe(viewLifecycleOwner) { status ->
                    val (success, exception) = status
                    if (success) {
                        val fragment = InviteFriend()
                        val bundle = Bundle().apply {
                            putString("success_message", "Post created successfully! Choose to invite a friend now!")
                        }
                        fragment.arguments = bundle

                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragmentContainerView, fragment)
                            ?.addToBackStack(null)
                            ?.commit()
                    } else {
                        Toast.makeText(requireContext(), "Failed to create post: ${exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }

//                val fragment = InviteFriend()
//                val transaction = activity?.supportFragmentManager?.beginTransaction()
//                transaction?.replace(R.id.fragmentContainerView, fragment)
//                transaction?.addToBackStack(null)
//                transaction?.commit()
//                val post = createPostObject()
//
//                val fragment = InviteFriend()
//                val bundle = Bundle().apply {
//                    putParcelable("post", post as Parcelable?)
//                }
//                fragment.arguments = bundle
//
//                val transaction = activity?.supportFragmentManager?.beginTransaction()
//                transaction?.replace(R.id.fragmentContainerView, fragment)
//                transaction?.addToBackStack(null)
//                transaction?.commit()

//                val fragment = InviteFriend()
//                val transaction = activity?.supportFragmentManager?.beginTransaction()
//                transaction?.replace(R.id.fragmentContainerView, fragment)
//                transaction?.addToBackStack(null)
//                transaction?.commit()
            }
        }

        btnAddCreatePost.setOnClickListener {
            pickImageFromGallery()
        }



        return view
    }

    object PostSingleton {
        var post: Post? = null
    }


    private fun createPostObject(): Post {
        val title = txtTitleCreatePost.text.toString()
        val description = txtDescriptionCreatePost.text.toString()
        val link = txtLinkCreatePost.text.toString()
        val category = ddlCategoryCreatePost.selectedItem.toString()
        val learningStyle = ddlLearningStyleCreatePost.selectedItem.toString()
        val currentDateTime = Calendar.getInstance().time.toString()
        val userID = getCurrentUserID()
        val postID = FirebaseDatabase.getInstance().reference.push().key
            ?: throw IllegalStateException("Post ID could not be generated")

        return Post(
            postID = postID,
            userID = userID,
            postImage = "",
            postTitle = title,
            postDescription = description,
            postLink = link,
            postCategory = category,
            postLearningStyle = learningStyle,
            postDateTime = currentDateTime
        )
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val title = txtTitleCreatePost.text.toString()
        if (title.isBlank()) {
            txtTitleCreatePost.error = "Title cannot be empty"
            isValid = false
        }

        val description = txtDescriptionCreatePost.text.toString()
        if (description.isBlank()) {
            txtDescriptionCreatePost.error = "Description cannot be empty"
            isValid = false
        }

        val category = ddlCategoryCreatePost.selectedItemPosition
        if (category == 0) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val learningStyle = ddlLearningStyleCreatePost.selectedItemPosition
        if (learningStyle == 0) {
            Toast.makeText(requireContext(), "Please select a learning style", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun savePostToFirebase() {
        val post = createPostObject()
        postViewModel.addPost(post, imageUri, getCurrentUserID())
    }



    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                imageView.setImageURI(imageUri)
                cardView2CreatePost.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentUserID(): String {
        return SaveSharedPreference.getUserID(requireContext())
    }
}
