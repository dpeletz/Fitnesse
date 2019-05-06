package com.example.fitnesse.data

data class Workout(
    var workoutID: String,
    var userID: String,
    var name: String,
    var exercises: List<Exercise>,
    var duration: Float,
    var reps: Int,
    var sets: Int,
    var volume: Int
)