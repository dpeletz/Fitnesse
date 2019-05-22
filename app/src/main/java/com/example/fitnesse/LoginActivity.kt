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
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            tilEmail.text.toString(), tilPassword.text.toString()
        ).addOnSuccessListener {
            val user = it.user
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userNameFromEmail(user.email!!))
                    .build()
            )

        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Register failed ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loginClick() {
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            tilEmail.text.toString(), tilPassword.text.toString()
        ).addOnSuccessListener {

            startActivity(
                Intent(this@LoginActivity, MainActivity::class.java)
            )
        }.addOnFailureListener {
            Toast.makeText(
                this@LoginActivity,
                "Login failed ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isFormValid(): Boolean {
        return when {
            tilEmail.text!!.isEmpty() -> {
                tilEmail.error = getString(R.string.empty_email_string)
                false
            }
            tilPassword.text!!.isEmpty() -> {
                tilPassword.error = getString(R.string.empty_password_string)
                false
            }
            else -> true
        }
    }

    private fun userNameFromEmail(email: String) = email.substringBefore(getString(R.string.at_symbol_string))
}