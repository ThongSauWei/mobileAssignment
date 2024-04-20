package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.FilterAdapter

class FilterPost : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter_post, container, false)

        val recyclerViewCategory : RecyclerView = view.findViewById(R.id.recyclerViewCategoryFilterPost)
        recyclerViewCategory.adapter = FilterAdapter(resources.getStringArray(R.array.category).toList())
        recyclerViewCategory.setHasFixedSize(true)

        val recyclerViewLearningStyle : RecyclerView = view.findViewById(R.id.recyclerViewLearningStyleFilterPost)
        recyclerViewLearningStyle.adapter = FilterAdapter(resources.getStringArray(R.array.learning_style).toList())
        recyclerViewLearningStyle.setHasFixedSize(true)

        return view
    }
}