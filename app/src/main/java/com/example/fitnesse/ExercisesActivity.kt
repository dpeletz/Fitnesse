package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.fitnesse.adapter.ExercisesAdapter
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_exercises.*
import kotlinx.android.synthetic.main.activity_exercises.navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_edit_exercise.*
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import kotlinx.android.synthetic.main.exercise_item.*
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

        btn_add_exercise.setOnClickListener {
            addFragmentPopup()
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

//            var listItems: List<Exercise> = listOf(
//                Exercise("exercise1", "user1", "Squat", false, 1),
//                Exercise("exercise2", "user1", "Bench Press", false, 2)
//            )
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

    private fun getExerciseUUID(): String {
        var string_UUID = UUID.randomUUID().toString()
        return string_UUID
    }

    private fun addExercise(name: String, description: String, radioButton: Int) {

        //TODO: link this up to correct information from an add exercise dialog or activity
        val exercise = Exercise(
            FirebaseAuth.getInstance().currentUser!!.uid,
            getExerciseUUID(),
            name,
            description,
            radioButton,
            false,
            2, true
        )

//        var exercisesCollection = FirebaseFirestore.getInstance().collection(
//            "exercises"
//        )
        var exercisesCollection =
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")
//        var exercisesCollection = FirebaseFirestore.getInstance().collection("users").document("exercises")
//            .collection(FirebaseAuth.getInstance().currentUser!!.uid.toString())


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

//        val query = db.collection("users").document("exercises")
//            .collection(FirebaseAuth.getInstance().currentUser!!.uid.toString())
        val query = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")

////        val query = db.collection("exercises")
//        var userId = FirebaseAuth.getInstance().currentUser!!.uid
//        println(userId)
////        println(FirebaseAuth.getInstance().docu)
//        println("----------")
//        val usersQuery = db.collection("users")

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
                val name = view.name_et.text.toString()
                val description = view.description_et.text.toString()
                val radioButton = view.radioGroup.checkedRadioButtonId
                val time = view.secs_et.text.toString()
                val reps = view.reps_et.text.toString()
                addExercise(name, description, radioButton)

//                if (radioButton == 1) {
//                    addExercise(name, description, radioButton)
//                }
//                if (radioButton == 2) {
//                    addExercise(name, description, radioButton)
//                }
                // TODO: give values to addExercise so that the data can be saved
                // TODO: also should we check for empty edit texts?
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


}
