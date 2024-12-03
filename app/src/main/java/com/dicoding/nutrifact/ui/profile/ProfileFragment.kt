package com.dicoding.nutrifact.ui.profile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.FragmentProfileBinding
import com.dicoding.nutrifact.ui.UserViewModelFactory
import com.dicoding.nutrifact.ui.ViewModelFactory
import com.dicoding.nutrifact.ui.addnew.NewNutrition
import com.dicoding.nutrifact.ui.editprofile.EditProfileActivity
import com.dicoding.nutrifact.ui.login.LoginActivity
import com.dicoding.nutrifact.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels {
        UserViewModelFactory.getInstance(requireContext())
    }
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogOut.setOnClickListener {
            authViewModel.logout()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.btnFaq.setOnClickListener {
        }

        binding.btnEdtProfile.setOnClickListener{
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        observeProfile()
        profileViewModel.getProfile()

        return root
    }

    private fun observeProfile() {
        profileViewModel.profileResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    val profile = result.data
                    binding.tvUsername.text = profile.data?.name
                    binding.tvEmail.text = profile.data?.email
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

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}