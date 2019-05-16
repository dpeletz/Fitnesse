package com.example.fitnesse.data

import java.util.Date

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var gender: Int = -1,
    var age: Int = -1,
    var height: Float = -1F,
    var weight: Float = -1F,
    var workouts: List<Workout> = listOf(Workout()),
    var exercises: List<Exercise> = listOf(Exercise())
)