package com.example.fitnesse.data

data class Exercise(
    var userID: String = "",
    var exerciseID: String = "",
    var name: String = "",
    var description: String = "",
    var value: Int = -1,
    var bodyweight: Boolean = true,
    var bodyPartCategory: Int = -1
)