package com.example.fitnesse.data

import android.service.autofill.FillEventHistory
import java.util.*

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