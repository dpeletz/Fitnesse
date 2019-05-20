package com.example.fitnesse.data

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var gender: Int = -1,
    var age: Int = -1,
    var height: Float = -1F,
    var weight: Float = -1F
)