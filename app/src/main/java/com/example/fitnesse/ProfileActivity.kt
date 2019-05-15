package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    var editMode = true
    lateinit var editTexts : List<TextInputEditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
    }
}
