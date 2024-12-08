package com.dicoding.nutrifact.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.ui.MainActivity
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityLoginBinding
import com.dicoding.nutrifact.viewmodel.UserViewModelFactory
import com.dicoding.nutrifact.ui.register.RegisterActivity
import com.dicoding.nutrifact.viewmodel.AuthViewModel


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        UserViewModelFactory.getInstance(this)
    }
    private var loadingDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkLoginStatus()

        binding.btnLogin.setOnClickListener {
            validateInputs()
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkLoginStatus() {
        authViewModel.getSession().observe(this) { user ->
            if (user.token != "") {
                finishAffinity()
                startActivity(Intent(this, MainActivity::class.java))
                Log.d("LoginTokenCheck", user.token)
            }
        }
    }

    private fun validateInputs() {
        val isValidEmail = binding.edLoginEmail.validate()
        val isValidPassword = binding.edLoginPassword.validate()
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()

        if (isValidEmail && isValidPassword) {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        authViewModel.login(email, password).observe(this, Observer { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Login Failed")
                        .setContentText(result.error)
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                        .show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (loadingDialog == null || !loadingDialog!!.isShowing) {
                loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
                    titleText = "Loading"
                    setCancelable(false)
                    show()
                }
            }
        } else {
            loadingDialog?.dismissWithAnimation()
            loadingDialog = null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.edLoginEmail.text?.clear()
        binding.edLoginPassword.text?.clear()
        binding.edLoginEmail.clearFocus()
        binding.edLoginPassword.clearFocus()
        binding.edLoginEmail.error = null
        binding.edLoginPassword.error = null
    }
}
