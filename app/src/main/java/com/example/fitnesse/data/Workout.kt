package com.example.fitnesse.data

import java.util.Date
import java.util.Stack

data class Workout(
    var workoutID: String,
    var userID: String,
    var name: String,
    var exercises: List<Exercise>,
    var duration: Float,
    var history: Stack<Date>,
    var totalReps: Int,
    var totalSets: Int,
    var totalVolume: Int
)