package com.example.fitnesse.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnesse.R
import com.example.fitnesse.WorkoutActivity
import com.example.fitnesse.data.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_edit_workout.view.*
import kotlinx.android.synthetic.main.workout_item.view.*

class WorkoutsAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>() {

    private var workouts = mutableListOf<Workout>()
    private var workoutKeys = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
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
            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtra(context.getString(R.string.workout_id_string), workout.workoutID)
            intent.putExtra(context.getString(R.string.workout_name_string), workout.name)
            context.startActivity(intent)
        }
        viewHolder.btnDeleteWorkout.setOnClickListener {
            removeWorkout(viewHolder.adapterPosition)
        }

        viewHolder.btnEditWorkout.setOnClickListener {
            editFragmentPopup(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name!!
        val btnView = itemView.btn_view_workout!!
        val btnDeleteWorkout = itemView.btn_delete_workout!!
        val btnEditWorkout = itemView.btn_edit_workout!!
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

    private fun removeWorkout(index: Int) {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(context.getString(R.string.workouts_string))
            .document(workoutKeys[index]).delete()
        workouts.removeAt(index)
        workoutKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    private fun updateWorkout(index: Int, newWorkout: Workout) {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(context.getString(R.string.workouts_string))
            .document(workoutKeys[index])
            .update(
                context.getString(R.string.name_string), newWorkout.name,
                context.getString(R.string.description_string), newWorkout.description
            )
            .addOnSuccessListener {
                workouts[index] = newWorkout
                notifyItemChanged(index)
            }
    }

    @SuppressLint("InflateParams")
    private fun editFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_workout, null)
        view.tvAddEditPrompt.text = context.getString(R.string.edit_workout_string)
        view.name_et.setText(workouts[position].name)
        view.description_et.setText(workouts[position].description)
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(context.getString(R.string.update_string)) { dialog, which ->
                val workout = workouts[position]
                workout.name = view.name_et.text.toString()
                workout.description = view.description_et.text.toString()

                updateWorkout(position, workout)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel_string)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}