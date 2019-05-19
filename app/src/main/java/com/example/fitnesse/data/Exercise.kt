package com.example.fitnesse.data

data class Exercise(
    var userID: String = "",
    var exerciseID: String = "",
    var name: String = "",
    var description: String = "",
    var value: Int = -1,
    var sets: Int = -1,
    var recordList: List<Int> = listOf(-1),
    var bodyweight: Boolean = true,
    var bodyPartCategory: Int = -1,
    var isMeasuredWithReps: Boolean = true
)