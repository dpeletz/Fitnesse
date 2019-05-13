package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.adapter.WorkoutAdapter
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_exercises.*
import kotlinx.android.synthetic.main.activity_workouts.recyclerList
import kotlinx.android.synthetic.main.workout_item.*

class ExercisesActivity : AppCompatActivity() {

    lateinit var exercisesAdapter: ExercisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        populateExerciseItems()

        exercisesAdapter = ExercisesAdapter(
            this,
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        btn_add_exercise.setOnClickListener {
            addExercise()
        }

//        btn_delete_exercise.setOnClickListener {
//            exercisesAdapter.removeExercise()
//        }
    }

    private fun populateExerciseItems() {
        Thread {
            /*
            TODO: incorporate Firebase here
             */

            initExercises()

            var listItems: List<Exercise> = listOf(
                Exercise("exercise1", "user1", "Squat", false, 1),
                Exercise("exercise2", "user1", "Bench Press", false, 2)
            )
//            var exercisesCollection = FirebaseFirestore.getInstance().collection(
//                "exercises"
//            )


            runOnUiThread {
                //                exercisesAdapter = ExercisesAdapter(this, listItems)
//                exercisesAdapter = ExercisesAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = exercisesAdapter
            }

        }.start()
    }

    private fun addExercise() {
        val exercise = Exercise(
            FirebaseAuth.getInstance().currentUser!!.uid,
            "1010221",
            "Bench Press",
            false,
            2
        )

        var exercisesCollection = FirebaseFirestore.getInstance().collection(
            "exercises"
        )

        exercisesCollection.add(
            exercise
        ).addOnSuccessListener {
            Toast.makeText(
                this@ExercisesActivity,
                "Exercise saved", Toast.LENGTH_LONG
            ).show()

//            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@ExercisesActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }

    }


    private fun initExercises() {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("exercises")

        var allExercisesListener = query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@ExercisesActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                val exercise = dc.document.toObject(Exercise::class.java)

                                exercisesAdapter.addExercise(exercise, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Toast.makeText(this@ExercisesActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG)
                                    .show()
                            }
                            DocumentChange.Type.REMOVED -> {
                                exercisesAdapter.removeExerciseByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }


}
