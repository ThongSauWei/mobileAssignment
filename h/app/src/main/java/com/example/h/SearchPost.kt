package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dao.PostDAO
import com.example.h.dataAdapter.PostAdapter
import com.example.h.repository.PostRepository
import com.example.h.viewModel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchPost : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_post, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewPostSearchPost)
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        val btnSearch: AppCompatButton = view.findViewById(R.id.btnSearchSearchPost)
        val txtSearch: EditText = view.findViewById(R.id.txtSearchSearchPost)

        btnSearch.setOnClickListener {
            val inputText = txtSearch.text.toString()
            searchPost(inputText)
        }


        // Initialize the PostAdapter
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // Initialize the ViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Fetch posts from Firebase and set them directly in the adapter
        fetchPosts()

        return view
    }

    private fun searchPost(inputText: String) {
        if (inputText.isEmpty()) {
            fetchPosts()
        } else {
            lifecycleScope.launch {
                try {
                    val posts = postViewModel.searchPost(inputText)
                    postAdapter.postList = posts
                    postAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchPosts() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val posts = postViewModel.getAllPost()
                postAdapter.postList = posts
                postAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}