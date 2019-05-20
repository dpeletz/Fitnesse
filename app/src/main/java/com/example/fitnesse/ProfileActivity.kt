package com.example.fitnesse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.widget.Toast
import com.example.fitnesse.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val genderArray = arrayOf("M", "F")
    var editMode = true
    lateinit var editTexts: List<TextInputEditText>
    lateinit var user: User

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
            if (btnEdit.text == "save") {
                sendValuesToFirebase()
            }
            switchEditMode()
        }

    }

    private fun sendValuesToFirebase() {
        //TODO: SHOULD WE ALLOW EMPTY VALUES???

        user.weight = etWeight.text.toString().toFloat()
        user.height = etHeight.text.toString().toFloat()
        user.gender = genderArray.indexOf(etGender.text.toString().toUpperCase())
        user.age = etAge.text.toString().toInt()
        user.name = etName.text.toString()

        if (etWeight.text.toString().toFloat() != 0F) {
            Log.d("height", user.height.toString())
            etBMI.text = (user.weight / (user.height * user.height)).toString() +
                    " kg / m^2"
        }

        addOrUpdateUser()
    }

    private fun addOrUpdateUser() {
        var usersCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("user")

        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObjects(User::class.java)
            if (user.size > 0) {

                usersCollection.document(documentSnapshot.documents.first().id)
                    .update(
                        (mapOf(
                            "weight" to etWeight.text.toString().toFloat(),
                            "height" to etHeight.text.toString().toFloat(),
                            "gender" to genderArray.indexOf(etGender.text.toString().toUpperCase()),
                            "age" to etAge.text.toString().toInt(),
                            "name" to etName.text.toString()
                        ))
                    )

            } else {
                addUserToCollection(usersCollection)
            }
        }
    }

    private fun loadUser() {
        var usersCollection = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection("user")

        usersCollection.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObjects(User::class.java)
            if (user.size > 0) {
                etName.setText(user.get(0).name)
                etWeight.setText(user.get(0).weight.toString())
                etHeight.setText(user.get(0).height.toString())
                etGender.setText(genderArray[user.get(0).gender])
                etAge.setText(user.get(0).age.toString())

                if (user.get(0).weight != 0F) {
                    etBMI.text = (user.get(0).weight / (user.get(0).height * user.get(0).height)).toString() +
                            " kg / m^2"
                }
            }
        }
    }

    private fun addUserToCollection(usersCollection: CollectionReference) {
        usersCollection.add(
            user
        ).addOnSuccessListener {
            Toast.makeText(
                this@ProfileActivity,
                "User saved", Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
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
        btnEdit.text = if (!editMode) "edit" else "save"
    }
}
