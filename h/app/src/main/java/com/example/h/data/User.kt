package com.example.h.data

data class User(
    val userID : String,
    val username : String,
    val userEmail : String,
    val userMobileNo : String,
    val userDOB : String,
    var userPassword : String,
    val userSecurityQuestion : String
) {
    constructor() : this("", "", "", "", "", "", "")
}
