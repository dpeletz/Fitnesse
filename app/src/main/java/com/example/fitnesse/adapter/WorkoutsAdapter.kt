package com.example.fitnesse.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.WorkoutActivity
import com.example.fitnesse.data.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_edit_workout.view.*
import kotlinx.android.synthetic.main.workout_item.view.*
import com.example.fitnesse.R
import com.google.firebase.auth.FirebaseAuth

class WorkoutsAdapter(private val context: Context, private val uId: String) :
    RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>() {

    private var workouts = mutableListOf<Workout>()
    private var workoutKeys = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        var itemRowView = LayoutInflater.from(context).inflate(
            com.example.fitnesse.R.layout.workout_item, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val workout = workouts[position]
        viewHolder.name.text = workout.name
        viewHolder.btnView.setOnClickListener { context.startActivity(Intent(context, WorkoutActivity::class.java)) }
        viewHolder.btnDeleteWorkout.setOnClickListener { removeWorkout(viewHolder.adapterPosition) }
        viewHolder.btnEditWorkout.setOnClickListener { editFragmentPopup(position) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnView = itemView.btn_view_workout
        val btnDeleteWorkout = itemView.btn_delete_workout
        val btnEditWorkout = itemView.btn_edit_workout
    }

    fun removeWorkoutByKey(key: String) {
        val index = workoutKeys.indexOf(key)
        if (index != -1) removeByIndex(index)
    }

    fun addWorkout(workout: Workout, key: String) {
        workouts.add(workout)
        workoutKeys.add(key)
        notifyDataSetChanged()
    }

    fun removeWorkout(index: Int) {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("workouts").document(workoutKeys[index]).delete()
        removeByIndex(index)
    }

    private fun removeByIndex(index: Int) {
        workouts.removeAt(index)
        workoutKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun updateWorkout(index: Int, newWorkout: Workout) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("workouts")
            .document(workoutKeys[index])
            .update(
                "name", newWorkout.name,
                "description", newWorkout.description
            )
            .addOnSuccessListener {
                workouts[index] = newWorkout
                notifyItemChanged(index)
            }
    }

    private fun editFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_workout, null)
        view.tvAddEditPrompt.text = "Edit Workout"
        view.name_et.setText(workouts[position].name)
        view.description_et.setText(workouts[position].description)
        setUpAlertDialog(view, position)
    }

    private fun setUpAlertDialog(view: View, position: Int) {
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Update") { dialog, which ->
                val workout = workouts[position]
                workout.name = view.name_et.text.toString()
                workout.description = view.description_et.text.toString()

                updateWorkout(position, workout)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}