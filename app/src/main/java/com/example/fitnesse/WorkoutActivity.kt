package com.example.fitnesse

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.fitnesse.adapter.AddExerciseAdapter
import com.example.fitnesse.adapter.WorkoutAdapter
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.add_exercise_in_workout.view.*

class WorkoutActivity : AppCompatActivity() {
    lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var workoutID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        tvTitle.text = intent.getStringExtra(getString(R.string.workout_name_intent))
        ManageBottomNavbar.setupNavbar(this@WorkoutActivity, navigation)

        workoutID = intent.getStringExtra(getString(R.string.workout_id_intent))

        populateExercises()
        btn_add_exercise.setOnClickListener { addFragmentPopup() }
    }

    private fun populateExercises() {
        Thread {
            runOnUiThread {
                workoutAdapter = WorkoutAdapter(this, workoutID)
                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutAdapter
                setupExercisesListener()
            }
        }.start()
    }

    @SuppressLint("InflateParams")
    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_exercise_in_workout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton(getString(R.string.cancel_string)) { dialog, which ->
                dialog.dismiss()
            }.show()
        val exercisesAdapter = AddExerciseAdapter(
            this,
            dialog,
            workoutID,
            workoutAdapter
        )
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
        val query = FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.exercises_string))

        query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@WorkoutActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.documentChanges) {
                        when (dc.type) {
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
        val query = FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.workouts_string))
            .document(workoutID)
            .collection(getString(R.string.exercises_string))

        query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@WorkoutActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.documentChanges) {
                        when (dc.type) {
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
