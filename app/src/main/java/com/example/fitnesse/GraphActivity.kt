package com.example.fitnesse

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.fitnesse.data.Exercise
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_graph.*
import kotlin.math.min

class GraphActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var spinner: Spinner? = null

    override fun onNothingSelected(parent: AdapterView<*>?) {
        val entries = ArrayList<Entry>()
        setLineChart(entries)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var selectedExerciseName = parent!!.getItemAtPosition(position)

        var exercisesCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")

        exercisesCollection
            .whereEqualTo("name", selectedExerciseName).get().addOnSuccessListener { documentSnapshot ->
                val exercise = documentSnapshot.toObjects(Exercise::class.java)

                var recordList = exercise.first().recordList

                val entries = ArrayList<Entry>()

                recordList.forEach { r -> entries.add(Entry(
                    recordList.indexOf(r).toFloat() + 1,
                    r.toFloat())) }

                setLineChart(entries)

            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        ManageBottomNavbar.setupNavbar(this@GraphActivity, navigation)

        val entries = ArrayList<Entry>()
        setLineChart(entries)

        spinner = this.spinnerGraphType
        spinner!!.setOnItemSelectedListener(this)

        val exercisesCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("exercises")

        exercisesCollection.get().addOnSuccessListener { documentSnapshot ->
            val exerciseList = documentSnapshot.toObjects(Exercise::class.java)

            val exerciseNameList = ArrayList<String>(exerciseList.size)
            exerciseList.forEach { it -> exerciseNameList.add(it.name) }

            val arrAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseNameList)
            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.setAdapter(arrAdapter)
        }
    }

    private fun setLineChart(entries: ArrayList<Entry>) {
        val lineDataSet = LineDataSet(entries, "Reps/Seconds")

        val maxY = entries.maxBy {
            it.y
        }?.y

        val data = LineData(lineDataSet)

        lineChart.data = data // set the data and list of labels into chart

        // TODO: set chart description
        lineChart.description.text = "Your Progress!"
        lineChart.description.textSize = 15F

        lineDataSet.color = resources.getColor(R.color.colorPrimary)
        lineDataSet.valueTextSize = 12F

        lineChart.animateY(1000)

        val leftAxis = lineChart.getAxisLeft()
        val rightAxis = lineChart.getAxisRight()
        leftAxis.axisMinimum = 0F
        leftAxis.axisMaximum = (maxY ?: 10F) * 1.5F
        rightAxis.axisMinimum = 0F
        rightAxis.axisMaximum = (maxY ?: 10F) * 1.5F
        rightAxis.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.axisMinimum = 0F
        xAxis.axisMaximum = maxOf(entries.size.toFloat() + 1F, 6F)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

    }
}