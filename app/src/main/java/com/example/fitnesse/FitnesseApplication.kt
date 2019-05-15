package com.example.fitnesse

import android.app.Application
import com.google.firebase.FirebaseApp

class FitnesseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }

}