package com.example.fitnesse.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.WorkoutActivity
import com.example.fitnesse.data.Workout
import kotlinx.android.synthetic.main.workout_item.view.*

class WorkoutsAdapter : RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {
    var workouts = mutableListOf<Workout>()

    private val context: Context

    // must call super in constructor as well
    constructor(context: Context, workoutItems : List<Workout>) : super() {
        this.context = context
        workouts.addAll(workoutItems)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        var itemRowView = LayoutInflater.from(context).inflate(
            R.layout.workout_item, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val workout = workouts[position]

        viewHolder.name.text = workout.name
        viewHolder.btnView.setOnClickListener {
            context.startActivity(Intent(context, WorkoutActivity::class.java))
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnView = itemView.btn_view_workout
    }
}