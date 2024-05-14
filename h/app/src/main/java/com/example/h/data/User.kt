package com.example.h.data

data class User(
    var userID : String,
    val username : String,
    val userEmail : String,
    val userMobileNo : String,
    val userDOB : String,
    val userPassword : String,
    val userSecurityQuestion : String
) {
    constructor() : this("", "", "", "", "", "", "")
}
