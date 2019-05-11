package com.example.fitnesse.data

import java.util.Date

data class User(
    var userID: String,
    var email: String,
    var password: String,
    var gender: Int,
    var age: Int,
    var birthday: Date,
    var height: Float,
    var weight: Float,
    var workouts: List<Workout>,
    var exercises: List<Exercise>
)