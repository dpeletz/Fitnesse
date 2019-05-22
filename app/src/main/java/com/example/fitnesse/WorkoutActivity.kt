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
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.activity_workout.btn_add_exercise
import kotlinx.android.synthetic.main.activity_workout.navigation
import kotlinx.android.synthetic.main.activity_workout.recyclerList
import kotlinx.android.synthetic.main.add_exercise_in_workout.view.*

class WorkoutActivity : AppCompatActivity() {

    lateinit var workoutAdapter: WorkoutAdapter
    lateinit var workoutID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        tvTitle.text = intent.getStringExtra("workoutName")
        ManageBottomNavbar.setupNavbar(this@WorkoutActivity, navigation)

        workoutID = intent.getStringExtra("workoutID")

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

            runOnUiThread {
                workoutAdapter = WorkoutAdapter(this, workoutID)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutAdapter

                setupExercisesListener()
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

        var exercisesAdapter = AddExerciseAdapter(this,
            dialog,
            workoutID,
            workoutAdapter)

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

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
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
                            }
                            DocumentChange.Type.REMOVED -> {
                                exercisesAdapter.removeExerciseByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }

    private fun setupExercisesListener() {

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("workouts")
            .document(workoutID)
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

                                workoutAdapter.addExercise(exercise, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                            DocumentChange.Type.REMOVED -> {
                                workoutAdapter.removeExerciseByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }
}
