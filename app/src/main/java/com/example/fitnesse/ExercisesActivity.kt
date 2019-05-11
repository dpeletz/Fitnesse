package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.adapter.WorkoutsAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.Workout
import kotlinx.android.synthetic.main.activity_workouts.*

class ExercisesActivity : AppCompatActivity() {

    lateinit var exercisesAdapter: ExercisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        populateExerciseItems()
    }

    private fun populateExerciseItems() {
        Thread {
            /*
            TODO: incorporate Firebase here
             */
            var listItems : List<Exercise> = listOf(
                Exercise("exercise1", "user1", "Squat", false, 1),
                Exercise("exercise2", "user1", "Bench Press", false, 2)
            )

            runOnUiThread {
                exercisesAdapter = ExercisesAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = exercisesAdapter
            }

        }.start()
    }
}
