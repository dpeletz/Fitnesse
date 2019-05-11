package com.example.fitnesse.data

data class ExerciseResult(
    var exerciseID: String,
    var workoutID: String,
    var userID: String,
    var weight: Float,
    var sets: Int,
    var reps: Int,
    var volume: Float
)