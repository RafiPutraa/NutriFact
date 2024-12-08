package com.dicoding.nutrifact.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityRegisterBinding
import com.dicoding.nutrifact.viewmodel.UserViewModelFactory
import com.dicoding.nutrifact.ui.login.LoginActivity
import com.dicoding.nutrifact.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels {
        UserViewModelFactory.getInstance(this)
    }
    private var loadingDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            validateInputs()
        }
    }

    private fun validateInputs() {
        val username = binding.edRegisterUsername.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val isValidUsername = binding.edRegisterUsername.validate()
        val isValidEmail = binding.edRegisterEmail.validate()
        val isValidPassword = binding.edRegisterPassword.validate()
        val isValidVerif = binding.edRegisterVerif.validate()

        Log.d(
            "RegisterActivity",
            "isValidUsername: $isValidUsername, isValidEmail: $isValidEmail, isValidPassword: $isValidPassword"
        )

        val isPasswordMatch =
            binding.edRegisterPassword.text.toString() == binding.edRegisterVerif.text.toString()

        if (!isPasswordMatch) {
            binding.edRegisterVerif.error = getString(R.string.error_password_not_match)
        } else {
            binding.edRegisterVerif.error = null
        }

        if (isValidUsername && isValidEmail && isValidPassword && isValidVerif && isPasswordMatch) {
            register(username, email, password)
        } else {
            Toast.makeText(this, "Register Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register(name: String, email: String, password: String) {
        authViewModel.register(name, email, password).observe(this, Observer { result ->
            Log.d("RegisterActivity", "Result: $result")
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Registration Successful")
                        .setContentText("You have successfully registered.\nPlease log in.")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                        }
                        .show()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Registration Failed")
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
}