package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stopwatch.*
import kotlinx.android.synthetic.main.time_row.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class StopwatchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        ManageBottomNavbar.setupNavbar(this@StopwatchActivity, navigation)

        btnMark.setOnClickListener {
            var now = System.currentTimeMillis()
            createTimeEntry(now)
        }

        btnStart.setOnClickListener {
            enabled = true

            mainTimer = Timer()

            mainTimer.schedule(MyTimerTask(), 0, 100)
        }

        btnStop.setOnClickListener {
            enabled = false
            lastElapsedTime = "0:0.0"

            mainTimer.cancel()
        }

        btnReset.setOnClickListener {
            enabled = false
            lastElapsedTime = "0:0.0"

            mainTimer.cancel()

            tvStopwatch.text = "0:0.0"
        }

    }

    fun getTimeToDisplay(new: Long = System.currentTimeMillis(), old: Long): String {
        val difference = new - old
        val minutes = TimeUnit.MILLISECONDS.toMinutes(difference)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(difference) - (60 * minutes)
        val deciseconds = (TimeUnit.MILLISECONDS.toMicros(difference) / 100000) - (10 * seconds) - (600 * minutes)

        return "$minutes:$seconds.$deciseconds"
    }

    var lastElapsedTime = "0:0.0"
    private var enabled = false


    inner class MyTimerTask : TimerTask() {
        var now = System.currentTimeMillis()
        override fun run() {
            runOnUiThread {
                tvStopwatch.text = getTimeToDisplay(old = now)
            }
        }
    }

    private lateinit var mainTimer: Timer

    private fun createTimeEntry(now: Long) {

        val myTimeView = layoutInflater.inflate(R.layout.time_row, null, false)

        var stopwatchText = tvStopwatch.text
        if (lastElapsedTime == "0:0.0") {
            myTimeView.tvTime.text = stopwatchText
            lastElapsedTime = now.toString()
        } else {
            myTimeView.tvTime.text = getTimeToDisplay(now, lastElapsedTime.toLong())
            lastElapsedTime = now.toString()
        }

        myTimeView.btnDelete.setOnClickListener {
            layoutContent.removeView(myTimeView)
        }

        layoutContent.addView(myTimeView, 0)
    }

    override fun onStop() {
        super.onStop()
        if (enabled) {
            mainTimer.cancel()
        }
        enabled = false


    }

}
