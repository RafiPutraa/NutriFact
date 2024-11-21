package com.dicoding.nutrifact.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.databinding.ActivityRegisterBinding
import com.dicoding.nutrifact.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            validateInputs()
        }

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ilUsername.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

        binding.etVerif.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ilVerif.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateInputs() {
        val usernameInput = binding.etUsername.text.toString().trim()
        val emailInput = binding.etEmail.text.toString().trim()
        val passwordInput = binding.etPassword.text.toString().trim()
        val verifInput = binding.etVerif.text.toString().trim()

        var isValid = true

        if (usernameInput.isEmpty()) {
            binding.ilUsername.error = getString(R.string.error_empty_username)
            isValid = false
        } else {
            binding.ilUsername.error = null
        }

        if (emailInput.isEmpty()) {
            binding.ilEmail.error = getString(R.string.error_empty_email)
            isValid = false
        } else {
            binding.ilEmail.error = null
        }

        if (passwordInput.isEmpty()) {
            binding.ilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (passwordInput.length < 8) {
            binding.ilPassword.error = getString(R.string.error_password_min_length)
            isValid = false
        } else {
            binding.ilPassword.error = null
        }

        if (verifInput.isEmpty()) {
            binding.ilVerif.error = getString(R.string.error_empty_verify_password)
            isValid = false
        } else if (verifInput != passwordInput) {
            binding.ilVerif.error = getString(R.string.error_password_not_match)
            isValid = false
        } else {
            binding.ilVerif.error = null
        }

        if (isValid) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}