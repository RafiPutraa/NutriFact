package com.dicoding.nutrifact.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutrifact.MainActivity
import com.dicoding.nutrifact.databinding.ActivityLoginBinding
import android.text.Editable
import android.text.TextWatcher
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

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

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ilEmail.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ilPassword.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateInputs() {
        val emailInput = binding.etEmail.text.toString().trim()
        val passwordInput = binding.etPassword.text.toString().trim()

        var isValid = true

        if (emailInput.isEmpty()) {
            binding.ilEmail.error = getString(R.string.error_empty_email)
            isValid = false
        } else {
            binding.ilEmail.error = null
        }

        if (passwordInput.isEmpty()) {
            binding.ilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else {
            binding.ilPassword.error = null
        }

        if (isValid) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()
    }
}
