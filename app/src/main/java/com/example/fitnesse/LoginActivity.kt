package com.example.fitnesse

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnRegister.setOnClickListener { registerClick() }
        btnSignIn.setOnClickListener { loginClick() }
    }

    private fun registerClick() {
        if (returnIfFormNotValid()) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            tilEmail.text.toString(), tilPassword.text.toString()
        ).addOnSuccessListener {
            val user = it.user
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userNameFromEmail(user.email!!))
                    .build()
            )
            Toast.makeText(
                this@LoginActivity,
                "REGISTER OK", Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Register failed ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loginClick() {
        if (returnIfFormNotValid()) return

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            tilEmail.text.toString(), tilPassword.text.toString()
        ).addOnSuccessListener {
            Toast.makeText(
                this@LoginActivity,
                "Login OK", Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Login failed ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun returnIfFormNotValid(): Boolean {
        if (!isFormValid()) {
            return true
        }
        return false
    }

    private fun isFormValid(): Boolean {
        return when {
            tilEmail.text!!.isEmpty() -> {
                tilEmail.error = "This field can not be empty"
                false
            }
            tilPassword.text!!.isEmpty() -> {
                tilPassword.error = "This field can not be empty"
                false
            }
            else -> true
        }
    }

    private fun userNameFromEmail(email: String) = email.substringBefore("@")
}