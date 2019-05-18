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

class GraphActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var exercises = arrayOf(
        "Exercise 1",
        "Exercise 2",
        "Exercise 3",
        "Exercise 4",
        "Exercise 5"
    )
    var spinner: Spinner? = null

    override fun onNothingSelected(parent: AdapterView<*>?) {
        println("NOTHING SELECTED YET")
        println("----------")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println(parent!!.getItemAtPosition(position))
        println("----------")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        ManageBottomNavbar.setupNavbar(this@GraphActivity, navigation)

        setLineChart()

        spinner = this.spinnerGraphType
        spinner!!.setOnItemSelectedListener(this)

        val exercisesCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("exercises")

        exercisesCollection.get().addOnSuccessListener { documentSnapshot ->
            val exerciseList = documentSnapshot.toObjects(Exercise::class.java)

            val exerciseNameList = ArrayList<String>(exerciseList.size)
            exerciseList.forEach { it -> exerciseNameList.add(it.name) }

            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseNameList)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.setAdapter(aa)
        }
    }

    private fun setLineChart() {
        val entries = ArrayList<Entry>()

        entries.add(Entry(1f, 135F))
        entries.add(Entry(8f, 155F))
        entries.add(Entry(15f, 160F))
        entries.add(Entry(22f, 175F))
        entries.add(Entry(29f, 180F))
        entries.add(Entry(36f, 180F))
        entries.add(Entry(50f, 195F))
        entries.add(Entry(64f, 225F))

        val lineDataSet = LineDataSet(entries, "Cells")

        val data = LineData(lineDataSet)

        lineChart.data = data // set the data and list of labels into chart

        // TODO: set chart description
        lineChart.description.text = "Set Line Chart Description"

        lineDataSet.color = resources.getColor(R.color.colorPrimary)

        lineChart.animateY(5000)

        val leftAxis = lineChart.getAxisLeft()
        val rightAxis = lineChart.getAxisRight()
        leftAxis.axisMinimum = 0F
        leftAxis.axisMaximum = 250F
        rightAxis.axisMinimum = 0F
        rightAxis.axisMaximum = 250F
        rightAxis.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.axisMinimum = 0F
        xAxis.axisMaximum = 70F
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }
}