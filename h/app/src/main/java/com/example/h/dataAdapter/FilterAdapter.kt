package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R

class FilterAdapter (private val list : List<String>) : RecyclerView.Adapter <FilterAdapter.FilterHolder>() {

    class FilterHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val btn : Button = itemView.findViewById(R.id.btnFilterFilterHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filter_holder, parent, false)

        return FilterHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FilterHolder, position: Int) {
        val currentItem = list[position]

        holder.btn.text = currentItem
    }


}