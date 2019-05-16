package com.example.fitnesse

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_main.*

object ManageBottomNavbar {

    fun setupNavbar(context : Context, navigation: BottomNavigationView) {
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_timer -> {
                    // TODO: launch timer activity here
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(context, ProfileActivity::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}