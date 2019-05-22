package com.example.fitnesse

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_stopwatch.*
import kotlinx.android.synthetic.main.time_row.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class StopwatchActivity : AppCompatActivity() {
    private lateinit var lastElapsedTime: String
    private var enabled = false
    private lateinit var mainTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)
        ManageBottomNavbar.setupNavbar(this@StopwatchActivity, navigation)
        lastElapsedTime = getString(R.string.zero_string)

        btnMark.setOnClickListener {
            val now = System.currentTimeMillis()
            createTimeEntry(now)
        }
        btnStart.setOnClickListener {
            enabled = true
            mainTimer = Timer()
            mainTimer.schedule(MyTimerTask(), 0, 100)
        }
        btnStop.setOnClickListener {
            enabled = false
            lastElapsedTime = getString(R.string.zero_string)
            mainTimer.cancel()
        }
        btnReset.setOnClickListener {
            enabled = false
            lastElapsedTime = getString(R.string.zero_string)
            mainTimer.cancel()
            tvStopwatch.text = getString(R.string.zero_string)
        }
    }

    override fun onStop() {
        super.onStop()
        if (enabled) {
            mainTimer.cancel()
        }
        enabled = false
    }

    inner class MyTimerTask : TimerTask() {
        private var now = System.currentTimeMillis()
        override fun run() {
            runOnUiThread { tvStopwatch.text = getTimeToDisplay(old = now) }
        }
    }

    fun getTimeToDisplay(new: Long = System.currentTimeMillis(), old: Long): String {
        val difference = new - old
        val minutes = TimeUnit.MILLISECONDS.toMinutes(difference)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(difference) - (60 * minutes)
        val deciseconds = (TimeUnit.MILLISECONDS.toMicros(difference) / 100000) - (10 * seconds) - (600 * minutes)

        return "$minutes:$seconds.$deciseconds"
    }

    @SuppressLint("InflateParams")
    private fun createTimeEntry(now: Long) {
        val myTimeView = layoutInflater.inflate(R.layout.time_row, null, false)

        val stopwatchText = tvStopwatch.text
        if (lastElapsedTime == getString(R.string.zero_string)) {
            myTimeView.tvTime.text = stopwatchText
            lastElapsedTime = now.toString()
        } else {
            myTimeView.tvTime.text = getTimeToDisplay(now, lastElapsedTime.toLong())
            lastElapsedTime = now.toString()
        }
        myTimeView.btnDelete.setOnClickListener { layoutContent.removeView(myTimeView) }
        layoutContent.addView(myTimeView, 0)
    }
}