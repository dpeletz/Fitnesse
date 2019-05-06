package com.example.fitnesse.data

data class Exercise (
    var exerciseID: String,
    var userID: String,
    var name : String,
    var time : Int,
    var bodyweight: Boolean,
    var bodyPartCategory: Int,
    var reps : Int
)