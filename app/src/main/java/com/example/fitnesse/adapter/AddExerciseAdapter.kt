package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitnesse.R
import com.example.fitnesse.WorkoutActivity
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercise_name_item.view.*

class AddExerciseAdapter(
    private val context: Context,
    private val dialog: AlertDialog
) : RecyclerView.Adapter<AddExerciseAdapter.ViewHolder>() {

    private var exercises = mutableListOf<Exercise>()
    private var exerciseKeys = mutableListOf<String>()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.exercise_name_item, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = exercises[position]

        viewHolder.btnExercise.text = exercise.name
        viewHolder.btnExercise.setOnClickListener {
            //TODO: ADD CLICKED EXERCISE
            //DataModel.selectedWorkout?.exercises!!.plus(exercise)
            (context as WorkoutActivity).addExercise(exercise)

            dialog.dismiss()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnExercise: Button = itemView.btnExercise
    }

    fun addExercise(exercise: Exercise, key: String) {
        exercises.add(exercise)
        exerciseKeys.add(key)
        notifyDataSetChanged()
    }

    fun removeExercise(index: Int) {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
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
}