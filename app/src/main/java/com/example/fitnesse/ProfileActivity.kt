package com.example.fitnesse

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TextInputEditText
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.navigation

class ProfileActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_timer -> {
                // TODO: launch timer activity here
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    var editMode = true
    lateinit var editTexts : List<TextInputEditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_profile

        editTexts = listOf<TextInputEditText>(
            etName,
            etGender,
            etAge,
            etHeight,
            etWeight
        )

        switchEditMode()

        btnEdit.setOnClickListener{
            switchEditMode()
        }
    }

    private fun switchEditMode() {
        editMode = !editMode
        for (editText in editTexts) {
            editText.isEnabled = editMode
            editText.setTextIsSelectable(editMode)
        }
        btnEdit.text = if (!editMode) "edit" else "save"
        // TODO: store changes in firebase
    }
}
