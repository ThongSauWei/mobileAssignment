package com.example.h.data

data class Group(
    var groupID : String,
    val groupName : String,
    val groupImage : String,
    val groupGoogleMeetLink : String
) {
    constructor() : this("", "", "", "")
}
