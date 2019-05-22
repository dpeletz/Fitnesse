package com.example.fitnesse.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import com.example.fitnesse.R
import com.example.fitnesse.data.Exercise
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.add_edit_exercise.view.*
import kotlinx.android.synthetic.main.exercise_item.view.*

class ExercisesAdapter(
    private val context: Context
) : RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    private var exercises = mutableListOf<Exercise>()
    private var exerciseKeys = mutableListOf<String>()

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
        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
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


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val exercise = exercises[position]

        viewHolder.name.text = exercise.name
        viewHolder.btnDeleteExercise.visibility = View.VISIBLE

        viewHolder.btnDeleteExercise.setOnClickListener {
            removeExercise(viewHolder.adapterPosition)
        }

        viewHolder.btnEditExercise.setOnClickListener {
            editFragmentPopup(position)
        }

        viewHolder.btnViewExercise.setOnClickListener {
            viewFragmentPopup(position)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name
        val btnDeleteExercise = itemView.btn_delete_workout
        val btnEditExercise = itemView.btn_edit_workout
        val btnViewExercise = itemView.btn_view_exercise
    }

    fun updateExercise(index: Int, newExercise: Exercise) {
        var exercisesCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")
        exercisesCollection.document(exerciseKeys[index]).update(
            "name", newExercise.name,
            "description", newExercise.description,
            "value", newExercise.value,
            "isMeasuredWithReps", newExercise.isMeasuredWithReps,
            "sets", newExercise.sets,
            "recordList", newExercise.recordList
        ).addOnSuccessListener {
                exercises[index] = newExercise
                notifyItemChanged(index)
            }



    }

    private fun editFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_exercise, null)

        view.tvAddEditPrompt.text = "Edit Exercise"
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

        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Update") { dialog, which ->
                val exercise = exercises[position]
                exercise.name = view.name_et.text.toString()
                exercise.description = view.description_et.text.toString()
                exercise.isMeasuredWithReps = (view.radioGroup.checkedRadioButtonId == R.id.rb_reps)
                exercise.sets = view.sets_et.text.toString().toInt()

                val oldValue = exercise.value
                if (view.radioGroup.checkedRadioButtonId == R.id.rb_reps) {
                    exercise.value = view.reps_et.text!!.toString().toInt()
                } else if (view.radioGroup.checkedRadioButtonId == R.id.rb_secs) {
                    exercise.value = view.secs_et.text!!.toString().toInt()
                }

                if (exercise.value != oldValue) {
                    val newRecordList = mutableListOf<Int>()
                    newRecordList.addAll(exercise.recordList)
                    newRecordList.add(exercise.value)
                    exercise.recordList = newRecordList
                }

                updateExercise(position, exercise)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun viewFragmentPopup(position: Int) {
        val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.add_edit_exercise, null)

        view.tvAddEditPrompt.text = "Exercise Details"
        view.name_et.setText(exercises[position].name)
        view.description_et.setText(exercises[position].description)
        if (exercises[position].isMeasuredWithReps) {
            view.radioGroup.check(view.rb_reps.id)
            view.reps_et.setText(exercises[position].value.toString())
        } else {
            view.radioGroup.check(view.rb_secs.id)
            view.secs_et.setText(exercises[position].value.toString())
        }
        view.sets_et.setText(exercises[position].sets.toString())

        switchEditMode(listOf(
            view.name_et,
            view.description_et,
            view.sets_et,
            view.reps_et,
            view.secs_et
        ), listOf(
            view.rb_secs,
            view.rb_reps
        ))

        AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Done") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun switchEditMode(editTexts: List<EditText>, radioButtons: List<RadioButton>) {
        for (editText in editTexts) {
            editText.isEnabled = false
            editText.setTextIsSelectable(false)
        }
        for (radioButton in radioButtons) {
            radioButton.isClickable = false
            radioButton.isEnabled = false
        }
    }
}