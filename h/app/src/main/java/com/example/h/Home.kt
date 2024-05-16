package com.example.h

import FilterPost
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.PostAdapter
import com.example.h.repository.PostRepository
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Home : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postViewModel: PostViewModel
    private lateinit var userViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val tvCriteriaHome = view.findViewById<TextView>(R.id.tvCriteriaHome)
        val tvCriteria2Home = view.findViewById<TextView>(R.id.tvCriteria2Home)

        // Retrieve selected data from arguments
        arguments?.let {
            val selectedCategory = arguments?.getString("selectedCategory")
            val selectedLearningStyle = arguments?.getString("selectedLearningStyle")
            val cardViewCriteriaHome = view.findViewById<CardView>(R.id.cardViewCriteriaHome)
            val cardViewCriteria2Home = view.findViewById<CardView>(R.id.cardViewCriteria2Home)

            if (selectedCategory != null && selectedLearningStyle != null) {
                tvCriteriaHome.text = selectedCategory.toString()
                cardViewCriteriaHome.visibility = View.VISIBLE

                tvCriteria2Home.text = selectedLearningStyle.toString()
                cardViewCriteria2Home.visibility = View.VISIBLE
            }
        }

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
            val fragment = FilterPost()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null) // Add to back stack
            transaction?.commit()
        }

        // Initialize the PostAdapter
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // Initialize the ViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Fetch posts
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
                val posts = postViewModel.getAllPost()
                postAdapter.setViewModel(userViewModel)
                postAdapter.postList = posts
                postAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchPosts1(category : String, learningStyle : String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = postViewModel.getPostByCategoryAndLearningStyle(category, learningStyle)
                postAdapter.setViewModel(userViewModel)
                postAdapter.postList = posts
                postAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
