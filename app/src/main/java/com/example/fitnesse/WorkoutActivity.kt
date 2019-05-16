package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.fitnesse.adapter.WorkoutAdapter
import com.example.fitnesse.data.Exercise
import kotlinx.android.synthetic.main.activity_workout.*

class WorkoutActivity : AppCompatActivity() {

    lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        populateExercises()
    }

    private fun populateExercises() {
        Thread {
            /*
            TODO: incorporate Firebase here
             */
            var listItems: List<Exercise> = listOf(
                Exercise("exercise3", "user1", "Bench Press", "", 0, false, 2, false)
            )

            runOnUiThread {
                workoutAdapter = WorkoutAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutAdapter
            }

        }.start()
    }
}
