package com.example.h.repository

import com.example.h.dao.GroupChatLineDAO
import com.example.h.data.GroupChatLine

class GroupChatLineRepository(private val groupChatLineDao : GroupChatLineDAO) {

    fun addGroupChatLine(groupChatLine : GroupChatLine) {
        groupChatLineDao.addGroupChatLine(groupChatLine)
    }

    suspend fun getGroupChatLine(groupID : String) : List<GroupChatLine> {
        return groupChatLineDao.getGroupChatLine(groupID)
    }
}