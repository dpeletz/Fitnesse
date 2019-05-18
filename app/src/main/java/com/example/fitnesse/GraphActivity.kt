package com.example.fitnesse

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.activity_graph.*

class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        ManageBottomNavbar.setupNavbar(this@GraphActivity, navigation)

        setLineChart()
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