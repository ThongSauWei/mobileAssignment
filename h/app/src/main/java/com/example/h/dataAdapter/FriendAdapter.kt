package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R

class FriendAdapter : RecyclerView.Adapter <FriendAdapter.FriendHolder>() {

    class FriendHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_holder, parent, false)

        return FriendHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {

    }
}