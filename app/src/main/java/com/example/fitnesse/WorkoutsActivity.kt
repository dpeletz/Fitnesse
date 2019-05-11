package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.fitnesse.adapter.WorkoutsAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.Workout
import kotlinx.android.synthetic.main.activity_workouts.*
import kotlinx.android.synthetic.main.workout_item.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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
            var exerciseList = listOf(
                Exercise("exercise1", "user1", "Squat", false, 1),
                Exercise("exercise2", "user1", "Bench Press", false, 2)
            )

            val date = Date(2019, 3, 4)
            val stackHistory = Stack<Date>()
            stackHistory.push(date)

            val listItems: List<Workout> = listOf(
                Workout("workout1", "user1", "Upper Body", exerciseList, 45F, stackHistory, 5, 5, 5)
            )

            runOnUiThread {
                workoutsAdapter = WorkoutsAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutsAdapter
            }

        }.start()
    }
}
