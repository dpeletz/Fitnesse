package com.example.fitnesse.data

import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.listOf

data class Workout(
    var userID: String = "",
    var workoutID: String = "",
    var name: String = "",
    var exercises: List<Exercise> = listOf(),
    var history: ArrayList<Date> = ArrayList(),
    var totalReps: Int = -1,
    var totalSets: Int = -1,
    var totalVolume: Int = -1,
    var description: String = ""
)