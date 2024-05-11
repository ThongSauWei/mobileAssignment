package com.example.h.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.h.R
import com.example.h.viewModel.FriendViewModel
import java.lang.IllegalStateException

class DeleteFriendDialog : DialogFragment() {

    lateinit var viewModel : FriendViewModel
    lateinit var friendID : String
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomAlertDialog)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.delete_friend_dialog, null)

            val btnYes : Button = view.findViewById(R.id.btnYesDeleteFriendDialog)
            val btnNo : Button = view.findViewById(R.id.btnNoDeleteFriendDialog)
            val imgClose : ImageView = view.findViewById(R.id.imgCloseDeleteFriendDialog)

            btnYes.setOnClickListener {
                viewModel.deleteFriend(friendID)
                dismiss()
            }

            btnNo.setOnClickListener {
                dismiss()
            }

            imgClose.setOnClickListener {
                dismiss()
            }

            builder.setView(view)

            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}