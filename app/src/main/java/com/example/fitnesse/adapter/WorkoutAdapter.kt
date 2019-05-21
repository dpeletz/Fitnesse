package com.example.fitnesse.adapter

import android.annotation.TargetApi
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercise_preview.view.*

@TargetApi(24)
class WorkoutAdapter : RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private val context: Context
    private var exercises = mutableListOf<Exercise>()
    private var workoutID: String

    // must call super in constructor as well
    constructor(context: Context, workoutID: String) : super() {
        this.context = context
        this.workoutID = workoutID
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        var itemRowView = LayoutInflater.from(context).inflate(
            R.layout.exercise_preview, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(viewHolder: WorkoutAdapter.ViewHolder, position: Int) {
        val exercise = exercises[position]
        viewHolder.name.text = exercise.name
        viewHolder.btnDelete.setOnClickListener{
            deleteExerciseFromDB(position)
        }
    }


    private fun deleteExerciseFromDB(index: Int) {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("workouts")
            .document(workoutID)
            .collection("exercises")
            .document(exercises[index].exerciseID).delete()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnDelete = itemView.btn_delete_workout
    }

    fun addExercise(exercise: Exercise, key: String) {
        exercise.exerciseID = key
        exercises.add(exercise)
        notifyDataSetChanged()
    }

    fun removeExerciseByKey(key: String) {
        var index = exercises.indexOfFirst {
            it.exerciseID == key
        }
        exercises.removeIf {
            it.exerciseID == key
        }
        if (index != -1) {
            notifyItemRemoved(index)
        }
    }
}