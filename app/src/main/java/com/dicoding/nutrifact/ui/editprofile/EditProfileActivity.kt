package com.dicoding.nutrifact.ui.editprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityEditProfileBinding
import com.dicoding.nutrifact.viewmodel.ViewModelFactory
import com.dicoding.nutrifact.ui.profile.ProfileFragment
import com.dicoding.nutrifact.util.reduceFileImage
import com.dicoding.nutrifact.util.uriToFile
import com.dicoding.nutrifact.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeProfile()
        observeImageUri()
        observeUpdateProfile()
        profileViewModel.getProfile()
    }

    private fun setupAction() {
        binding.imgProfile.setOnClickListener { startGallery() }
        binding.btnSave.setOnClickListener { updateProfile() }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profileViewModel.setCurrentImageUri(uri)
            Log.d("Gallery", "Image selected: $uri")
        } else {
            Log.d("Gallery", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun updateProfile() {
        val name = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Name and password cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = profileViewModel.currentImageUri.value
        if (uri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val file = uriToFile(uri,this@EditProfileActivity).reduceFileImage()
            val nameBody = name.toRequestBody("text/plain".toMediaType())
            val passwordBody = password.toRequestBody("text/plain".toMediaType())
            val imageBody = MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody("image/jpeg".toMediaType())
            )
            profileViewModel.updateProfile(nameBody, passwordBody, imageBody)
        }
    }


    private fun observeUpdateProfile() {
        profileViewModel.updateProfileResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    Log.d("UpdateProfile", "Profile update is loading")
                    showLoading(true)
                }

                is ResultState.Success -> {
                    Log.d("UpdateProfile", "Profile updated successfully")
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Profile Updated")
                        .setContentText("Your profile has been updated successfully")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            supportFragmentManager.beginTransaction()
                                .replace(android.R.id.content, ProfileFragment())
                                .commit()
                            finish()
                        }
                        .show()
                }
                is ResultState.Error -> {
                    Log.e("UpdateProfile", "Error updating profile: ${result.error}")
                    showLoading(false)
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Edit Profile Failed")
                        .setContentText(result.error)
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                        }
                        .show()
                }
            }
        }
    }

    private fun observeProfile() {
        profileViewModel.profileResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val profile = result.data
                    binding.etUsername.setText(profile.data?.name)
                    Glide.with(this)
                        .load(profile.data?.profileImageURL)
                        .into(binding.imgProfile)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("ProfileFragment", result.error)
                }
            }
        }
    }

    private fun observeImageUri() {
        profileViewModel.currentImageUri.observe(this) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .into(binding.imgProfile)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
