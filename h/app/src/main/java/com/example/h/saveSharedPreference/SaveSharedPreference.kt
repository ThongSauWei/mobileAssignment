package com.example.h.saveSharedPreference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SaveSharedPreference {
    private const val PREF_USER_ID = "userID"

    private fun getSharedPreference(context : Context) : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setUserID(context : Context, userID : String) {
        val editor = getSharedPreference(context).edit()
        editor.putString(PREF_USER_ID, userID)
        editor.apply()
    }

    fun getUserID(context : Context) : String {
        return getSharedPreference(context).getString(PREF_USER_ID, "U100")?: "U100"
    }
}