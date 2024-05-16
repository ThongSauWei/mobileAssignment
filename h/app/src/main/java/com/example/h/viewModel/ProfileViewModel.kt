package com.example.h.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.h.dao.ProfileDAO
import com.example.h.data.Profile
import com.example.h.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application : Application) : AndroidViewModel(application) {
    private val profileRepository : ProfileRepository

    init {
        val profileDao = ProfileDAO()
        profileRepository = ProfileRepository(profileDao)
    }

    fun addProfile(profile : Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.addProfile(profile)
        }
    }

    suspend fun getProfile(userID : String) : Profile? {
        return profileRepository.getProfile(userID)
    }

    suspend fun getUserListByCourse(course : String) : List<String> {
        return profileRepository.getUserListByCourse(course)
    }


    fun deleteProfile(userID : String) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.deleteProfile(userID)
        }
    }
}