package com.example.fitnesse

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_exercises.*
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import java.util.*

class ExercisesActivity : AppCompatActivity() {

    lateinit var exercisesAdapter: ExercisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        ManageBottomNavbar.setupNavbar(this@ExercisesActivity, navigation)

        populateExerciseItems()

        exercisesAdapter = ExercisesAdapter(this)
        btn_add_exercise.setOnClickListener { addFragmentPopup() }
    }

    private fun populateExerciseItems() {
        Thread {
            initExercises()

            runOnUiThread {
                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = exercisesAdapter
            }
        }.start()
    }

    private fun addExercise(exercise: Exercise) {
        val exercisesCollection =
            FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection(getString(R.string.exercises_string))

        exercisesCollection.add(
            exercise
        ).addOnSuccessListener {
            exercise.exerciseID = it.id
        }.addOnFailureListener {
            Toast.makeText(
                this@ExercisesActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun initExercises() {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.exercises_string))

        var allExercisesListener = query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@ExercisesActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
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

    @SuppressLint("InflateParams")
    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_edit_exercise, null)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(getString(R.string.done_string)) { dialog, which ->
                val name = view.name_et.text.toString()
                val description = view.description_et.text.toString()
                val radioButton = view.radioGroup.checkedRadioButtonId
                val sets = view.sets_et.text.toString().toInt()

                val exercise = Exercise(
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    UUID.randomUUID().toString(),
                    name,
                    description,
                    0,
                    sets,
                    listOf(),
                    false,
                    2,
                    (radioButton == R.id.rb_reps)
                )
                if (radioButton == R.id.rb_reps) {
                    exercise.value = view.reps_et.text!!.toString().toInt()
                } else if (radioButton == R.id.rb_secs) {
                    exercise.value = view.secs_et.text!!.toString().toInt()
                }
                exercise.recordList = listOf(exercise.value)
                addExercise(exercise)

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_string)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}