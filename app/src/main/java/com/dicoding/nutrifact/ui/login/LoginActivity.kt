package com.dicoding.nutrifact.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dicoding.nutrifact.MainActivity
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityLoginBinding
import com.dicoding.nutrifact.ui.ViewModelFactory
import com.dicoding.nutrifact.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            validateInputs()
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs() {
        val isValidEmail = binding.edLoginEmail.validate()
        val isValidPassword = binding.edLoginPassword.validate()
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        Log.d("LoginActivity", "isValidEmail: $isValidEmail, isValidPassword: $isValidPassword")

        if (isValidEmail && isValidPassword) {
            login(email, password)
        } else {
            Toast.makeText(this, "Please enter valid email and password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, password: String) {
        loginViewModel.login(email, password).observe(this, Observer { result ->
            Log.d("LoginActivity", "Result: $result")
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    showToast(result.error)
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        binding.edLoginEmail.text?.clear()
        binding.edLoginPassword.text?.clear()
        binding.edLoginEmail.clearFocus()
        binding.edLoginPassword.clearFocus()
    }
}
