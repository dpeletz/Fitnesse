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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_graph.*

class GraphActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var spinner: Spinner? = null

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // TODO: allow graph to just be first exercise in list (if one exists)
        println("NOTHING SELECTED YET")
        println("----------")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // TODO: switch graph to the selected exercise
        var selectedExerciseName = parent!!.getItemAtPosition(position)
        var exercisesCollection =
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("exercises")
        queryExercisesCollection(exercisesCollection, selectedExerciseName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        ManageBottomNavbar.setupNavbar(this@GraphActivity, navigation)

        setUpGenericChart()
        spinner = this.spinnerGraphType
        spinner!!.setOnItemSelectedListener(this)
        setUpExerciseSpinner()
    }

    private fun queryExercisesCollection(
        exercisesCollection: CollectionReference,
        selectedExerciseName: Any?
    ) {
        exercisesCollection
            .whereEqualTo("name", selectedExerciseName).get().addOnSuccessListener { documentSnapshot ->
                val exercise = documentSnapshot.toObjects(Exercise::class.java)
                var recordList = exercise.first().recordList
                val entries = ArrayList<Entry>()
                recordList.forEach { r -> entries.add(Entry(recordList.indexOf(r).toFloat(), r.toFloat())) }
                setLineChart(entries, exercise.first().name)
            }
    }

    private fun setUpExerciseSpinner() {
        val exercisesCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("exercises")

        exercisesCollection.get().addOnSuccessListener { documentSnapshot ->
            val exerciseList = documentSnapshot.toObjects(Exercise::class.java)

            // TODO: potentially add in check to verify that at least 1 exercise is in list
            val exerciseNameList = ArrayList<String>(exerciseList.size)
            exerciseList.forEach { it -> exerciseNameList.add(it.name) }
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseNameList)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.setAdapter(aa)
        }
    }

    private fun setUpGenericChart() {
        val entries = ArrayList<Entry>()

        entries.add(Entry(1f, 135F))
        entries.add(Entry(2f, 155F))
        entries.add(Entry(3f, 160F))
        entries.add(Entry(4f, 175F))
        entries.add(Entry(5f, 180F))
        entries.add(Entry(6f, 180F))
        entries.add(Entry(7f, 195F))
        entries.add(Entry(8f, 225F))
        entries.add(Entry(9f, 235F))
        setLineChart(entries, "initial")
    }

    private fun setLineChart(entries: ArrayList<Entry>, name: String) {
        val lineDataSet = LineDataSet(entries, "Cells")
        val data = LineData(lineDataSet)
        setUpLineChart(data, name, lineDataSet)
        setGraphAxes()
    }

    private fun setUpLineChart(
        data: LineData,
        name: String,
        lineDataSet: LineDataSet
    ) {
        lineChart.data = data // set the data and list of labels into chart
        // TODO: set chart description
        lineChart.description.text = "Line Chart of $name"
        lineDataSet.color = resources.getColor(R.color.colorPrimary)
        lineChart.animateY(5000)
    }

    private fun setGraphAxes() {
        val leftAxis = lineChart.getAxisLeft()
        val rightAxis = lineChart.getAxisRight()
        leftAxis.axisMinimum = 0F
        leftAxis.axisMaximum = 250F
        rightAxis.axisMinimum = 0F
        rightAxis.axisMaximum = 250F
        rightAxis.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.axisMinimum = 0F
        xAxis.axisMaximum = 10F
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }
}