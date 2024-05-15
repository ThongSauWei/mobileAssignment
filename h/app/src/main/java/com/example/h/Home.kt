package com.example.h

import FilterPost
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dao.PostDAO
import com.example.h.data.Post
import com.example.h.dataAdapter.PostAdapter
import com.example.h.repository.PostRepository
import com.example.h.viewModel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postRepository: PostRepository
    private lateinit var postViewModel: PostViewModel

    private var selectedCategory: String? = null
    private var selectedLearningStyle: String? = null


    //private lateinit var btnAddHome: AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvCriteriaHome = view.findViewById<TextView>(R.id.tvCriteriaHome)
        val tvCriteria2Home = view.findViewById<TextView>(R.id.tvCriteria2Home)

        // Retrieve selected data from arguments
        arguments?.let {
            val selectedCategory = arguments?.getString("selectedCategory")
            val selectedLearningStyle = arguments?.getString("selectedLearningStyle")
            //selectedCategory = it.getString("selectedCategory")
            //selectedLearningStyle = it.getString("selectedLearningStyle")

            val cardViewCriteriaHome = view.findViewById<CardView>(R.id.cardViewCriteriaHome)
            val cardViewCriteria2Home = view.findViewById<CardView>(R.id.cardViewCriteria2Home)

            if (selectedCategory != null && selectedLearningStyle != null) {
                Log.d("selectedCategoryH", selectedCategory)
                tvCriteriaHome.text = selectedCategory.toString()
                cardViewCriteriaHome.visibility = View.VISIBLE

//            if (selectedLearningStyle != null) {
                Log.d("selectedLearningStyleH", selectedLearningStyle)
                tvCriteria2Home.text = selectedLearningStyle.toString()
                cardViewCriteria2Home.visibility = View.VISIBLE

                //fetchPostsCategory()
            }

        }


        //btnAddHome = view.findViewById(R.id.btnAddHome)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewPostHome)
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        val btnAddHome = view.findViewById<ImageView>(R.id.btnAddHome)
        btnAddHome.setOnClickListener {
            val fragment = CreatePost()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        val btnFilterHome = view.findViewById<ImageView>(R.id.btnFilterHome)
        btnFilterHome.setOnClickListener {
            // Navigate to FilterPost fragment
            val fragment = FilterPost()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null) // Add to back stack
            transaction?.commit()

        }

        // Initialize the PostAdapter
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // Initialize the PostRepository
        postRepository = PostRepository(PostDAO())

        // Fetch posts from Firebase and set them directly in the adapter
        //fetchPosts()

//        if (!selectedCategory.isNullOrBlank() && !selectedLearningStyle.isNullOrBlank()) {
//            fetchPostsByCategoryAndLearningStyle()
//        } else {
//            fetchPosts()
//        }

        if (tvCriteriaHome.text == "ALL") {
            fetchPosts()
        } else {
            fetchPosts1(tvCriteriaHome.text.toString(), tvCriteria2Home.text.toString())
        }

        return view
    }

    private fun fetchPosts() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = postRepository.getAllPost()
                // Set the fetched posts directly in the adapter
                postAdapter.postList = posts
                postAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }

    private fun fetchPosts1(category : String, learningStyle : String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = postRepository.getPostByCategoryAndLearningStyle(category, learningStyle)
                // Set the fetched posts directly in the adapter
                postAdapter.postList = posts
                postAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
            }
        }
    }

//    private fun fetchPostsByCategoryAndLearningStyle() {
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                val posts = selectedCategory?.let {
//                    selectedLearningStyle?.let { it1 ->
//                        postViewModel.getPostByCategoryAndLearningStyle(
//                            it, it1
//                        )
//                    }
//                }
//                // Set the fetched posts directly in the adapter
//                if (posts != null) {
//                    postAdapter.postList = posts
//                }
//                postAdapter.notifyDataSetChanged()
//            } catch (e: Exception) {
//                // Handle exception
//                e.printStackTrace()
//            }
//        }
//    }
}