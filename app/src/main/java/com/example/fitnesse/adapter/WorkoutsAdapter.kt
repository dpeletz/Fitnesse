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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.workout_item.view.*

class WorkoutsAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>() {

    //class WorkoutsAdapter : RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {
    private var workouts = mutableListOf<Workout>()
    private var workoutKeys = mutableListOf<String>()

//    private val context: Context

    // must call super in constructor as well
//    constructor(context: Context, workoutItems: List<Workout>) : super() {
//        this.context = context
//        workouts.addAll(workoutItems)
//    }

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
        viewHolder.btnDeleteWorkout.setOnClickListener {
            removeWorkout(viewHolder.adapterPosition)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnView = itemView.btn_view_workout

        val btnDeleteWorkout = itemView.btn_delete_workout
    }


    fun removeWorkoutByKey(key: String) {
        val index = workoutKeys.indexOf(key)
        if (index != -1) {
            workouts.removeAt(index)
            workoutKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addWorkout(workout: Workout, key: String) {
        workouts.add(workout)
        workoutKeys.add(key)
        notifyDataSetChanged()
    }

    fun removeWorkout(index: Int) {
        FirebaseFirestore.getInstance().collection("workouts").document(
            workoutKeys[index]
        ).delete()

        workouts.removeAt(index)
        workoutKeys.removeAt(index)
        notifyItemRemoved(index)
    }


}