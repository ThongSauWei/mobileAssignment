package com.example.h

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.h.dao.PostDAO
import com.example.h.data.Post
import com.example.h.repository.PostRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

    private val postRepository = PostRepository(PostDAO())

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

        btnNextCreatePost.setOnClickListener {
            if (validateForm()) {
                savePostToFirebase()
            }
//            if (validateForm()) {
//                val post = createPostObject() // Create a Post object with the required data
//                val action = CreatePostFragmentDirections.actionCreatePostFragmentToInviteFriendFragment(post)
//                findNavController().navigate(action)
//            }


        }

        btnAddCreatePost.setOnClickListener {
            pickImageFromGallery()
        }

        return view
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
        val title = txtTitleCreatePost.text.toString()
        val description = txtDescriptionCreatePost.text.toString()
        val link = txtLinkCreatePost.text.toString()
        val category = ddlCategoryCreatePost.selectedItem.toString()
        val learningStyle = ddlLearningStyleCreatePost.selectedItem.toString()
        val currentDateTime = Calendar.getInstance().time.toString()
        val userID = getCurrentUserID()
        val postID = FirebaseDatabase.getInstance().reference.push().key
            ?: throw IllegalStateException("Post ID could not be generated")

        val post = Post(
            postID = postID,
            userID = userID,
            postImage = "",
            postTitle = title,
            postDescription = description,
            postCategory = category,
            postLearningStyle = learningStyle,
            postDateTime = currentDateTime
        )

        lifecycleScope.launch {
            try {
                postRepository.addPost(post, imageUri, userID) { success, exception ->
                    if (success) {
                        Toast.makeText(requireContext(), "Post saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to save post: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to save post: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        return "A100" //testing data
    }
}

