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
import kotlinx.android.synthetic.main.activity_exercises.*
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
//            var exerciseList = listOf(
//                Exercise("exercise1", "user1", "Squat", false, 1),
//                Exercise("exercise2", "user1", "Bench Press", false, 2)
//            )

            val date = Date(2019, 3, 4)
            val stackHistory = Stack<Date>()
            stackHistory.push(date)
//
//            val listItems: List<Workout> = listOf(
//                Workout("workout1", "user1", "Upper Body", exerciseList, 45F, stackHistory, 5, 5, 5)
//            )

            runOnUiThread {
                //                workoutsAdapter = WorkoutsAdapter(this, listItems)

                recyclerList.layoutManager = LinearLayoutManager(this)
                recyclerList.adapter = workoutsAdapter
            }

        }.start()
    }

    private fun getWorkoutUUID(): String {
        var string_UUID = UUID.randomUUID().toString()
        return string_UUID
    }

    private fun addWorkout(name: String, description: String) {

//        val exercise = FirebaseFirestore.getInstance().collection("users").document("exercises")
//            .collection(FirebaseAuth.getInstance().currentUser!!.uid.toString())

        //TODO: CHANGE THIS FROM BEING HARD CODED!
        val exercise = Exercise(
            FirebaseAuth.getInstance().currentUser!!.uid,
            "1010221",
            "Bench Press",
            "",
            0,
            false,
            2, true
        )

        val exerciseList = listOf(exercise)

        //TODO: CHANGE THE HISTORY STACK
        val historyStack = Stack<Date>()
        historyStack.push(Date(2019, 5, 5))

        //TODO: link this up to correct information from an add workout dialog or activity
//        val workout = Workout(
//            FirebaseAuth.getInstance().currentUser!!.uid,
//            "WORKOUT_ID",
//            "WORKOUT_NAME",
//            exerciseList,
//            55F,
//            arrayListOf(Date(2019, 5, 5)),
//            1,
//            1,
//            1
//        )
        val workout = Workout(
            FirebaseAuth.getInstance().currentUser!!.uid,
            getWorkoutUUID(),
            name,
            exerciseList,
            55F,
            arrayListOf(Date(2019, 5, 5)),
            1,
            1,
            1,
            description
        )

        var workoutsCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("workouts")
//        var workoutsCollection = FirebaseFirestore.getInstance().collection("users").document("workouts")
//            .collection(FirebaseAuth.getInstance().currentUser!!.uid.toString())


        workoutsCollection.add(
            workout
        ).addOnSuccessListener {
            Toast.makeText(
                this@WorkoutsActivity,
                "Workout saved", Toast.LENGTH_LONG
            ).show()

//            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@WorkoutsActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun initWorkouts() {
        val db = FirebaseFirestore.getInstance()

//        val query = db.collection("workouts")
//        val query = db.collection("users").document("workouts")
//            .collection(FirebaseAuth.getInstance().currentUser!!.uid.toString())
        var query = db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
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
                // TODO: give name and description to addWorkout so that the data can be saved
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
