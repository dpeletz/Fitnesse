package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.fitnesse.adapter.WorkoutsAdapter
import com.example.fitnesse.data.Workout
import kotlinx.android.synthetic.main.activity_workouts.*
import kotlinx.android.synthetic.main.workout_item.*

class WorkoutsActivity : AppCompatActivity() {

    lateinit var workoutsAdapter: WorkoutsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        populateWorkoutItems()
    }

    private fun populateWorkoutItems() {
        Thread {
            /*
            TODO: incorporate Firebase here
             */
            var listItems : List<Workout> = listOf(
                Workout("hi", listOf(), 0)
            )

            runOnUiThread {
                workoutsAdapter = WorkoutsAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutsAdapter
            }

        }.start()
    }
}
