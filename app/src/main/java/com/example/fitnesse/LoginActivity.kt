package com.example.fitnesse

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        /*
        TODO: update this with database login and such !!
         */
        btnSignIn.setOnClickListener{
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

}
