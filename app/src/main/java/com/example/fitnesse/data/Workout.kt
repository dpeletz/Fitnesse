package com.example.fitnesse.data

import java.util.Date
import java.util.Stack

data class Workout(
    var userID: String = "",
    var workoutID: String = "",
    var name: String = "",
    var exercises: List<Exercise> = listOf(Exercise()),
    var duration: Float = -1F,
//    var history: Stack<Date> = Stack(),
    //TODO: change this back to the Stack for history
    var history: ArrayList<Date> = ArrayList(),
    var totalReps: Int = -1,
    var totalSets: Int = -1,
    var totalVolume: Int = -1,
    var description: String = ""
)