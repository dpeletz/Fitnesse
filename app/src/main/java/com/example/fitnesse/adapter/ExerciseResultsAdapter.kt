package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.data.ExerciseResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExerciseResultsAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<ExerciseResultsAdapter.ViewHolder>() {

    private var exerciseResults = mutableListOf<ExerciseResult>()
    private var exerciseResultKeys = mutableListOf<String>()

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
        return exerciseResults.size
    }


    fun addExerciseResult(exerciseResult: ExerciseResult, key: String) {
        exerciseResults.add(exerciseResult)
        exerciseResultKeys.add(key)
        notifyDataSetChanged()
    }

    fun removeExerciseResult(index: Int) {
        FirebaseFirestore.getInstance().collection("exerciseResults").document(
            exerciseResultKeys[index]
        ).delete()

        exerciseResults.removeAt(index)
        exerciseResultKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeExerciseResultByKey(key: String) {
        val index = exerciseResultKeys.indexOf(key)
        if (index != -1) {
            exerciseResults.removeAt(index)
            exerciseResultKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exerciseResult = exerciseResults[position]

//        viewHolder.name.text = exerciseResult.name
//        viewHolder.btnDeleteExercise.visibility = View.VISIBLE
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val name = itemView.name
//        val btnDeleteExercise = itemView.btn_delete_exercise
    }
}