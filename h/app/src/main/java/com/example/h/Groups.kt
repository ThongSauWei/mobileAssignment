package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.GroupAdapter

class Groups : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbarGroups)
        toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).openDrawer()
        }


        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewGroupGroups)
        recyclerView.adapter = GroupAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        return view
    }
}