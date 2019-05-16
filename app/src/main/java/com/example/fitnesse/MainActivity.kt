package com.example.fitnesse

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.fitnesse.data.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ManageBottomNavbar.setupNavbar(this@MainActivity, navigation)

        btnWorkouts.setOnClickListener {
            startActivity(Intent(this@MainActivity, WorkoutsActivity::class.java))
        }

        btnExercises.setOnClickListener {
            startActivity(Intent(this@MainActivity, ExercisesActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }

        btnGraphs.setOnClickListener {
            startActivity(Intent(this@MainActivity, GraphActivity::class.java))
        }
    }

    override fun onRestart() {
        super.onRestart()
        navigation.selectedItemId = R.id.navigation_home
    }
}
