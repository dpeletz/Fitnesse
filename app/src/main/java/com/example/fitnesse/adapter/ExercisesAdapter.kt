package com.example.fitnesse.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import com.gc.materialdesign.views.ButtonIcon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExercisesAdapter(private val context: Context) :
    RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    private var exercises = mutableListOf<Exercise>()
    private var exerciseKeys = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemRowView = LayoutInflater.from(context).inflate(
            R.layout.exercise_item, viewGroup, false
        )
        return ViewHolder(itemRowView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = exercises[position]

        viewHolder.name.text = exercise.name
        viewHolder.btnDeleteExercise.visibility = View.VISIBLE

        viewHolder.btnDeleteExercise.setOnClickListener { removeExercise(viewHolder.adapterPosition) }

        viewHolder.btnEditExercise.setOnClickListener { editFragmentPopup(position) }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.name
        val btnDeleteExercise: ButtonIcon = itemView.btn_delete_workout
        val btnEditExercise: ButtonIcon = itemView.btn_edit_workout
    }

    fun addExercise(exercise: Exercise, key: String) {
        exercises.add(exercise)
        exerciseKeys.add(key)
        notifyDataSetChanged()
    }

    private fun removeExercise(index: Int) {
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")
            .document(exerciseKeys[index]).delete()

        removeByIndex(index)
    }

    fun removeExerciseByKey(key: String) {
        val index = exerciseKeys.indexOf(key)
        if (index != -1) {
            removeByIndex(index)
        }
    }

    private fun removeByIndex(index: Int) {
        exercises.removeAt(index)
        exerciseKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    private fun updateExercise(index: Int, newExercise: Exercise) {
        val exercisesCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")
        updateExerciseCollection(exercisesCollection, index, newExercise)
    }

    private fun updateExerciseCollection(
        exercisesCollection: CollectionReference,
        index: Int,
        newExercise: Exercise
    ) {
        exercisesCollection.document(exerciseKeys[index]).update(
            "name", newExercise.name,
            "description", newExercise.description,
            "value", newExercise.value,
            "isMeasuredWithReps", newExercise.isMeasuredWithReps,
            "sets", newExercise.sets
        ).addOnSuccessListener {
            exercises[index] = newExercise
            notifyItemChanged(index)
        }
    }

    @SuppressLint("InflateParams")
    private fun editFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_exercise, null)

        setUpView(view, position)

        setUpAlertDialog(view, position)
    }

    private fun setUpAlertDialog(view: View, position: Int) {
        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Update") { dialog, which ->
                val exercise = setExerciseValues(position, view)
                updateExercise(position, exercise)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setExerciseValues(position: Int, view: View): Exercise {
        val exercise = exercises[position]
        exercise.name = view.name_et.text.toString()
        exercise.description = view.description_et.text.toString()
        exercise.isMeasuredWithReps = (view.radioGroup.checkedRadioButtonId == R.id.rb_reps)
        exercise.sets = view.sets_et.text.toString().toInt()

        if (view.radioGroup.checkedRadioButtonId == R.id.rb_reps) {
            exercise.value = view.reps_et.text!!.toString().toInt()
        } else if (view.radioGroup.checkedRadioButtonId == R.id.rb_secs) {
            exercise.value = view.secs_et.text!!.toString().toInt()
        }
        return exercise
    }

    private fun setUpView(view: View, position: Int) {
        view.tvAddEditPrompt.text = context.getString(R.string.edit_exercise)
        view.name_et.setText(exercises[position].name)
        view.description_et.setText(exercises[position].description)
        // TODO: why does it go back to reps when activity is resumed
        if (exercises[position].isMeasuredWithReps) {
            view.radioGroup.check(view.rb_reps.id)
            view.reps_et.setText(exercises[position].value.toString())
        } else {
            view.radioGroup.check(view.rb_secs.id)
            view.secs_et.setText(exercises[position].value.toString())
        }
        view.sets_et.setText(exercises[position].sets.toString())
    }
}