package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val editTexts = listOf<TextInputEditText>(
            etName,
            etGender,
            etAge,
            etHeight,
            etWeight,
            etBMI
        )

        for (editText in editTexts) {
            editText.isEnabled = false
            editText.keyListener = null
            editText.setTextIsSelectable(false)
        }
    }
}
