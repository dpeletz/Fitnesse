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
import com.example.fitnesse.preferences.DataModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.add_exercise_in_workout.view.*

class WorkoutActivity : AppCompatActivity() {
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        ManageBottomNavbar.setupNavbar(this@WorkoutActivity, navigation)

        DataModel.selectedWorkout!!.exercises =
            DataModel.selectedWorkout!!.exercises.plus(Exercise(name = "Chest Press"))
        workoutAdapter = WorkoutAdapter(this, DataModel.selectedWorkout!!.exercises)

        populateExercises()
        btn_add_exercise.setOnClickListener { addFragmentPopup() }
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

//            listItems = FirebaseFirestore.getInstance().collection("users")
//                .document(FirebaseAuth.getInstance().currentUser!!.uid)
//                .collection("workouts")
// CODE BELOW SHOWS HOW TO QUERY THE EXERCISES LIST AND SEE IF THERE IS MORE THAN 1 EXERCISE:
//
//            exercisesCollection.get().addOnSuccessListener { documentSnapshot ->
//                val exercise = documentSnapshot.toObjects(Exercise::class.java)
//                if (exercise.size > 0) {
//                    listItems = exercise
//                }
//            }

            runOnUiThread {
                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutAdapter
            }

        }.start()
    }

    override fun onBackPressed() {
        DataModel.selectedWorkout?.exercises = listOf()
        DataModel.selectedWorkout = null
        super.onBackPressed()
    }

    @SuppressLint("InflateParams")
    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_exercise_in_workout, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }.show()

        val exercisesAdapter = AddExerciseAdapter(this, dialog)
        populateExerciseItems(exercisesAdapter, view)
    }

    fun addExercise(exercise: Exercise) {

        val query = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("workouts")
            .document("7NDTZtpwCjckOejmYNAl")
            .collection("exercises").add(exercise).addOnCompleteListener(
                object : OnCompleteListener<DocumentReference> {
                    override fun onComplete(p0: Task<DocumentReference>) {
                        workoutAdapter.addExerciseToWorkout(exercise, p0.result!!.id)
                    }
                }
            )
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
        val query = db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")
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