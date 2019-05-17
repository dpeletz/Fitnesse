package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.fitnesse.adapter.WorkoutAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
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
            var exercisesCollection =
                FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .collection("exercises")

            var listItems: List<Exercise> =
                listOf(Exercise(userID = FirebaseAuth.getInstance().currentUser!!.uid, name = "chest press"))


// CODE BELOW SHOWS HOW TO QUERY THE EXERCISES LIST AND SEE IF THERE IS MORE THAN 1 EXERCISE:
//
//            exercisesCollection.get().addOnSuccessListener { documentSnapshot ->
//                val exercise = documentSnapshot.toObjects(Exercise::class.java)
//                if (exercise.size > 0) {
//                    listItems = exercise
//                }
//            }

            runOnUiThread {
                workoutAdapter = WorkoutAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutAdapter
            }

        }.start()
    }
}
