package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import kotlinx.android.synthetic.main.exercise_preview.view.*

class WorkoutAdapter : RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    var exercises = mutableListOf<Exercise>()

    private val context: Context

    // must call super in constructor as well
    constructor(context: Context, exerciseItems: List<Exercise>) : super() {
        this.context = context
        exercises.addAll(exerciseItems)
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
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
    }
}