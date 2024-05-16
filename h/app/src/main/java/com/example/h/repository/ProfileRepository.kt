package com.example.h.repository

import com.example.h.dao.ProfileDAO
import com.example.h.data.Profile

class ProfileRepository(private val profileDao : ProfileDAO) {

    fun addProfile(profile : Profile) {
        profileDao.addProfile(profile)
    }

    suspend fun getProfile(userID : String) : Profile? {
        return profileDao.getProfile(userID)
    }

    suspend fun getUserListByCourse(course : String) : List<String> {
        return profileDao.getUserListByCourse(course)
    }

    suspend fun getRemainingUsers(userIDList : List<String>) : List<String> {
        return profileDao.getRemainingUsers(userIDList)
    }

    fun deleteProfile(userID : String) {
        profileDao.deleteProfile(userID)
    }
}