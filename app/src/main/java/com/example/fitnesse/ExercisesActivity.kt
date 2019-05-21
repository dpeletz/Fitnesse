package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_exercises.*
import kotlinx.android.synthetic.main.activity_exercises.navigation
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import java.util.*

class ExercisesActivity : AppCompatActivity() {
    lateinit var exercisesAdapter: ExercisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)
        ManageBottomNavbar.setupNavbar(this@ExercisesActivity, navigation)

        populateExerciseItems()
        exercisesAdapter = ExercisesAdapter(
            this,
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        btn_add_exercise.setOnClickListener { addFragmentPopup() }
    }

    private fun populateExerciseItems() {
        Thread {
            /*
            TODO: incorporate Firebase here
             */
            initExercises()
            runOnUiThread {
                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = exercisesAdapter
            }
        }.start()
    }

    private fun addExercise(exercise: Exercise) {
        var exercisesCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")

        queryExercisesCollection(exercisesCollection, exercise)
        addExercise(exercisesCollection, exercise)
    }

    private fun queryExercisesCollection(
        exercisesCollection: CollectionReference,
        exercise: Exercise
    ) {
        exercisesCollection
            .whereEqualTo("name", exercise.name).get().addOnSuccessListener { documentSnapshot ->
                val exercises = documentSnapshot.toObjects(Exercise::class.java)

                if (exercises.size > 0) {
                    println("EXERCISE ALREADY IN DB")
                    // TODO: change functionality when exercise already exists in DB
                }
                //                exercisesCollection.document(documentSnapshot.documents.first().id)
                //                    .update(
                //                        (mapOf(
                //                            "name" to name,
                //                            "description" to description
                //                        ))
                //                    )
                //                view.name.setText(name)
                //                view.description_et.setText(description)
            }
    }

    private fun addExercise(
        exercisesCollection: CollectionReference,
        exercise: Exercise
    ) {
        exercisesCollection.add(
            exercise
        ).addOnSuccessListener {
            exercise.exerciseID = it.id
            Toast.makeText(
                this@ExercisesActivity,
                "Exercise saved", Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                this@ExercisesActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initExercises() {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")
        getExercisesListener(query)
    }

    private fun getExercisesListener(query: CollectionReference) {
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

    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_edit_exercise, null)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Done") { dialog, which ->
                val exercise = getExerciseValues(view)
                addExercise(exercise)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getExerciseValues(view: View): Exercise {
        val name = view.name_et.text.toString()
        val description = view.description_et.text.toString()
        val radioButton = view.radioGroup.checkedRadioButtonId
        val sets = view.sets_et.text.toString().toInt()

        //TODO: change this generic value for record list
        val exercise = Exercise(
            FirebaseAuth.getInstance().currentUser!!.uid,
            UUID.randomUUID().toString(),
            name,
            description,
            0,
            sets,
            listOf(40, 50, 80, 90, 100),
            false,
            2,
            (radioButton == R.id.rb_reps)
        )

        if (radioButton == R.id.rb_reps) {
            exercise.value = view.reps_et.text!!.toString().toInt()
        } else if (radioButton == R.id.rb_secs) {
            exercise.value = view.secs_et.text!!.toString().toInt()
        }
        return exercise
    }
}