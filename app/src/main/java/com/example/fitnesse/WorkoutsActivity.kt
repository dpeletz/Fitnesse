package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.fitnesse.adapter.WorkoutsAdapter
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_workouts.*
import kotlinx.android.synthetic.main.activity_workouts.recyclerList
import kotlinx.android.synthetic.main.add_edit_workout.*
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

        btn_add_workout.setOnClickListener {
            addFragmentPopup()
        }

    }

    private fun populateWorkoutItems() {
        Thread {

            initWorkouts()
            /*
            TODO: incorporate Firebase here
             */

            val date = Date(2019, 3, 4)
            val stackHistory = Stack<Date>()
            stackHistory.push(date)

            runOnUiThread {
                //                workoutsAdapter = WorkoutsAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutsAdapter
            }

        }.start()
    }

    private fun getWorkoutUUID(): String {
        var stringUUID = UUID.randomUUID().toString()
        return stringUUID
    }

    private fun addWorkout(name: String, description: String) {

        val exerciseList = listOf(Exercise())
        val historyStack = Stack<Date>()
        historyStack.push(Date(2019, 5, 5))

        //TODO: link this up to correct information from an add workout dialog or activity
        val workout = Workout(
            FirebaseAuth.getInstance().currentUser!!.uid,
            getWorkoutUUID(),
            name,
            exerciseList,
            arrayListOf(Date(2019, 5, 5)),
            1,
            1,
            1,
            description
        )

        var workoutsCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("workouts")

        workoutsCollection.add(
            workout
        ).addOnSuccessListener {
            Toast.makeText(
                this@WorkoutsActivity,
                "Workout saved", Toast.LENGTH_LONG
            ).show()

        }.addOnFailureListener {
            Toast.makeText(
                this@WorkoutsActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun initWorkouts() {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("workouts")


        var allWorkoutsListener = query.addSnapshotListener(
            object : EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@WorkoutsActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                val workout = dc.document.toObject(Workout::class.java)

                                workoutsAdapter.addWorkout(workout, dc.document.id)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Toast.makeText(this@WorkoutsActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG)
                                    .show()
                            }
                            DocumentChange.Type.REMOVED -> {
                                workoutsAdapter.removeWorkoutByKey(dc.document.id)
                            }
                        }
                    }
                }
            })
    }

    private fun addFragmentPopup() {
        val view = layoutInflater.inflate(R.layout.add_edit_workout, null)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Done") { dialog, which ->
                val name = view.name_et.text.toString()
                val description = view.description_et.text.toString()
                // TODO: could not figure out how to check for empty input texts :(
                addWorkout(name, description)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


}
