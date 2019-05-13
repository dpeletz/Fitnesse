package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExercisesAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

//    private val context: Context
//class ExercisesAdapter : RecyclerView.Adapter<ExercisesAdapter.ViewHolder> {
//
//    private val context: Context

    private var exercises = mutableListOf<Exercise>()
    private var exerciseKeys = mutableListOf<String>()

    // must call super in constructor as well
//    constructor(context: Context, exerciseItems: List<Exercise>) : super() {
//        this.context = context
//        exercises.addAll(exerciseItems)
//    }

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
        FirebaseFirestore.getInstance().collection("exercises").document(
            exerciseKeys[index]
        ).delete()

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
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnDeleteExercise = itemView.btn_delete_workout
    }
}