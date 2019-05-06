package com.example.fitnesse.data

data class Workout (
    var name : String,
    var exercises : List<Exercise>,
    var sets : Int
)