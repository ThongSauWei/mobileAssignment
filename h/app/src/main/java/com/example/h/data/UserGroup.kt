package com.example.h.data

data class UserGroup(
    val userGroupID : String,
    val userID : String,
    val groupID : String,
    val lastSeen : String
) {
    constructor() : this("", "", "", "")
}
