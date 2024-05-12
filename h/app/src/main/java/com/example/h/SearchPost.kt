package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dao.PostDAO
import com.example.h.dataAdapter.PostAdapter
import com.example.h.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchPost : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postRepository: PostRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_search_post, container, false)
//
//        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewPostSearchPost)
//        recyclerView.adapter = PostAdapter()
//        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
//        recyclerView.setHasFixedSize(true)
//
//        return view
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_post, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewPostSearchPost)
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        // Initialize the PostAdapter
        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        // Initialize the PostRepository
        postRepository = PostRepository(PostDAO())

        // Fetch posts from Firebase and set them directly in the adapter
        fetchPosts()

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
}