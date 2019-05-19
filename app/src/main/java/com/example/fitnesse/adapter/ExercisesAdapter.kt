package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExercisesAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    private var exercises = mutableListOf<Exercise>()
    private var exerciseKeys = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        var itemRowView = LayoutInflater.from(context).inflate(
            R.layout.exercise_item, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }


    fun addExercise(exercise: Exercise, key: String) {
        exercises.add(exercise)
        exerciseKeys.add(key)
        notifyDataSetChanged()
    }

    fun removeExercise(index: Int) {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("exercises").document(exerciseKeys[index]).delete()

        exercises.removeAt(index)
        exerciseKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeExerciseByKey(key: String) {
        val index = exerciseKeys.indexOf(key)
        if (index != -1) {
            exercises.removeAt(index)
            exerciseKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = exercises[position]

        viewHolder.name.text = exercise.name
        viewHolder.btnDeleteExercise.visibility = View.VISIBLE

        viewHolder.btnDeleteExercise.setOnClickListener {
            removeExercise(viewHolder.adapterPosition)
        }

        viewHolder.btmEditExercise.setOnClickListener {
            editFragmentPopup(position)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnDeleteExercise = itemView.btn_delete_workout
        val btmEditExercise = itemView.btn_edit_workout
    }

    fun updateExercise(index: Int, name: String, description: String, view: View) {
        //TODO: implement this updateExercise function
//        var exercisesCollection = FirebaseFirestore.getInstance().collection("users")
//            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
//            .collection("exercise")
//
//        exercisesCollection
//            .whereEqualTo("name", name).get().addOnSuccessListener { documentSnapshot ->
//                val exercise = documentSnapshot.toObjects(Exercise::class.java)
//
//                exercise.first().description = description
////                exercisesCollection.document().update(
////                    (mapOf(
////                        "name" to name,
////                        "description" to description
////                    ))
////                )
//                view.name.setText(name)
//                view.description_et.setText(description)
//            }
    }

    private fun editFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_exercise, null)

        val oldName = view.name_et.text.toString()
        val oldDescription = view.description_et.text.toString()

        view.tvAddEditPrompt.text = "Edit Exercise"
        view.name_et.setText(exercises[position].name)
        view.description_et.setText(exercises[position].description)
        if (exercises[position].isMeasuredWithReps) {
            view.radioGroup.check(view.rb_reps.id)
            view.reps_et.setText(exercises[position].value.toString())
        } else {
            view.secs_et.setText(exercises[position].value.toString())
        }

        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Update") { dialog, which ->
                val name = view.name_et.text.toString()
                val description = view.description_et.text.toString()
                // TODO: give name and description to updateExercise so that the data can be saved
                updateExercise(position, oldName, oldDescription, view)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}