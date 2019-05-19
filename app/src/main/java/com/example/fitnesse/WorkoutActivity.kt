package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.fitnesse.adapter.AddExerciseAdapter
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.adapter.WorkoutAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_exercises.*
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.activity_workout.btn_add_exercise
import kotlinx.android.synthetic.main.activity_workout.navigation
import kotlinx.android.synthetic.main.activity_workout.recyclerList
import kotlinx.android.synthetic.main.add_exercise_in_workout.view.*

class WorkoutActivity : AppCompatActivity() {

    lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        ManageBottomNavbar.setupNavbar(this@WorkoutActivity, navigation)

        populateExercises()

        btn_add_exercise.setOnClickListener{
            addFragmentPopup()
        }
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

    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_exercise_in_workout, null)


        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }.show()

        var exercisesAdapter = AddExerciseAdapter(this, FirebaseAuth.getInstance().currentUser!!.uid, dialog)

        populateExerciseItems(exercisesAdapter, view)
    }

    private fun populateExerciseItems(exercisesAdapter: AddExerciseAdapter, view: View) {
        Thread {

            initExercises(exercisesAdapter)

            runOnUiThread {
                view.recyclerList.layoutManager = LinearLayoutManager(this)
                view.recyclerList.adapter = exercisesAdapter
            }

        }.start()
    }

    private fun initExercises(exercisesAdapter: AddExerciseAdapter) {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")

        query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@WorkoutActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                val exercise = dc.document.toObject(Exercise::class.java)

                                exercisesAdapter.addExercise(exercise, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Toast.makeText(this@WorkoutActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG)
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
