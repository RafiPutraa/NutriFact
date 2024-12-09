package com.dicoding.nutrifact.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.ui.login.LoginActivity
import com.dicoding.nutrifact.viewmodel.AuthViewModel
import com.dicoding.nutrifact.viewmodel.UserViewModelFactory

class SplashActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        UserViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Handler(Looper.getMainLooper()).postDelayed({
            authViewModel.getSession().observe(this) { user ->
                if (user.token != "") {
                    finishAffinity()
                    startActivity(Intent(this, MainActivity::class.java))
                    Log.d("LoginTokenCheck", user.token)
                } else {
                    finishAffinity()
                    startActivity(Intent(this, LoginActivity::class.java))
                }}
               },3000)
    }
}