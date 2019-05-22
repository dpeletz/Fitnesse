package com.example.fitnesse

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.fitnesse.adapter.WorkoutsAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_workouts.*
import kotlinx.android.synthetic.main.add_edit_workout.view.*
import java.util.*

class WorkoutsActivity : AppCompatActivity() {
    lateinit var workoutsAdapter: WorkoutsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)
        ManageBottomNavbar.setupNavbar(this@WorkoutsActivity, navigation)
        populateWorkoutItems()
        workoutsAdapter = WorkoutsAdapter(
            this,
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        btn_add_workout.setOnClickListener { addFragmentPopup() }
    }

    private fun populateWorkoutItems() {
        Thread {
            initWorkouts()
            runOnUiThread {
                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutsAdapter
            }
        }.start()
    }

    private fun addWorkout(name: String, description: String) {
        val workout = Workout(
            FirebaseAuth.getInstance().currentUser!!.uid,
            UUID.randomUUID().toString(),
            name,
            listOf(Exercise()),
            arrayListOf(Date(2019, 5, 5)),
            1,
            1,
            1,
            description
        )

        val workoutsCollection =
            FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection(getString(R.string.workouts_string))

        workoutsCollection.add(
            workout
        ).addOnSuccessListener {
            workout.workoutID = it.id
        }.addOnFailureListener {
            Toast.makeText(
                this@WorkoutsActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initWorkouts() {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.workouts_string))
        var allWorkoutsListener = query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@WorkoutsActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }
                    for (dc in querySnapshot!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                val workout = dc.document.toObject(Workout::class.java)

                                workoutsAdapter.addWorkout(workout, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                            DocumentChange.Type.REMOVED -> {
                                workoutsAdapter.removeWorkoutByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }

    @SuppressLint("InflateParams")
    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_edit_workout, null)
        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(getString(R.string.done_string)) { dialog, which ->
                val name = view.name_et.text.toString()
                val description = view.description_et.text.toString()
                // TODO: could not figure out how to check for empty input texts :(
                addWorkout(name, description)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_string)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}