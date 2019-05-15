package com.example.fitnesse.data

data class ExerciseResult(
    var userID: String = "",
    var exerciseID: String = "",
    var workoutID: String = "",
    var weight: Float = -1F,
    var sets: Int = -1,
    var reps: Int = -1,
    var volume: Float = -1F
)