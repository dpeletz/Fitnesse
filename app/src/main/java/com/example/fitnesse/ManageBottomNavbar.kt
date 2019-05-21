package com.example.fitnesse

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView

object ManageBottomNavbar {
    fun setupNavbar(context: Context, navigation: BottomNavigationView) {
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_timer -> {
                    val intent = Intent(context, StopwatchActivity::class.java)
                    context.startActivity(intent)
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

        when (context) {
            is MainActivity -> navigation.selectedItemId = R.id.navigation_home
            is StopwatchActivity -> navigation.selectedItemId = R.id.navigation_timer
            is ProfileActivity -> navigation.selectedItemId = R.id.navigation_profile
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}