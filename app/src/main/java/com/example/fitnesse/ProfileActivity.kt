package com.example.fitnesse

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val genderArray = arrayOf("M", "F")
    private var editMode = true
    private lateinit var editTexts: List<TextInputEditText>
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ManageBottomNavbar.setupNavbar(this@ProfileActivity, navigation)

        user = User(
            email = FirebaseAuth.getInstance().currentUser!!.email.toString(),
            userID = FirebaseAuth.getInstance().currentUser!!.uid
        )
        loadUser()
        editTexts = listOf<TextInputEditText>(
            etName,
            etGender,
            etAge,
            etHeight,
            etWeight
        )
        switchEditMode()
        btnEdit.setOnClickListener {
            if (btnEdit.text == getString(R.string.save_string)) {
                sendValuesToFirebase()
            }
            switchEditMode()
        }
    }

    private fun sendValuesToFirebase() {
        user.weight = etWeight.text.toString().toFloat()
        user.height = etHeight.text.toString().toFloat()
        user.gender = genderArray.indexOf(etGender.text.toString().toUpperCase())
        user.age = etAge.text.toString().toInt()
        user.name = etName.text.toString()

        if (etWeight.text.toString().toFloat() != 0F) {
            Log.d(getString(R.string.height_string), user.height.toString())
            etBMI.text = (user.weight / (user.height * user.height)).toString()
        }

        addOrUpdateUser()
    }

    private fun addOrUpdateUser() {
        val usersCollection = FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.user_string))

        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObjects(User::class.java)
            if (user.size > 0) {

                usersCollection.document(documentSnapshot.documents.first().id)
                    .update(
                        (mapOf(
                            getString(R.string.weight_string) to etWeight.text.toString().toFloat(),
                            getString(R.string.height_string) to etHeight.text.toString().toFloat(),
                            getString(R.string.gender_string) to genderArray.indexOf(etGender.text.toString().toUpperCase()),
                            getString(R.string.age_string) to etAge.text.toString().toInt(),
                            getString(R.string.name_string) to etName.text.toString()
                        ))
                    )

            } else {
                addUserToCollection(usersCollection)
            }
        }

    }

    private fun loadUser() {
        val usersCollection = FirebaseFirestore.getInstance().collection(getString(R.string.users_string))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.user_string))

        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObjects(User::class.java)
            if (user.size > 0) {
                etName.setText(user[0].name)
                etWeight.setText(user[0].weight.toString())
                etHeight.setText(user[0].height.toString())
                etGender.setText(genderArray[user[0].gender])
                etAge.setText(user[0].age.toString())

                if (user[0].weight != 0F) {
                    etBMI.text = (user[0].weight / (user[0].height * user[0].height)).toString()
                }

            }
        }
    }

    private fun addUserToCollection(usersCollection: CollectionReference) {
        usersCollection.add(
            user
        ).addOnFailureListener {
            Toast.makeText(
                this@ProfileActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun switchEditMode() {
        editMode = !editMode
        for (editText in editTexts) {
            editText.isEnabled = editMode
            editText.setTextIsSelectable(editMode)
        }
        btnEdit.text = if (!editMode) getString(R.string.edit_string) else getString(R.string.save_string)
    }
}