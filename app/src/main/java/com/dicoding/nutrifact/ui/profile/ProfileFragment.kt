package com.dicoding.nutrifact.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.local.HistoryRepository
import com.dicoding.nutrifact.data.local.room.HistoryDatabase
import com.dicoding.nutrifact.databinding.FragmentProfileBinding
import com.dicoding.nutrifact.viewmodel.UserViewModelFactory
import com.dicoding.nutrifact.viewmodel.ViewModelFactory
import com.dicoding.nutrifact.ui.editprofile.EditProfileActivity
import com.dicoding.nutrifact.ui.faq.FAQActivity
import com.dicoding.nutrifact.ui.login.LoginActivity
import com.dicoding.nutrifact.viewmodel.AuthViewModel
import com.dicoding.nutrifact.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyRepository: HistoryRepository
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

        val historyDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        historyRepository = HistoryRepository(historyDao)
        binding.btnLogOut.setOnClickListener {
            authViewModel.logout()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            requireActivity().finish()
            lifecycleScope.launch {
                historyRepository.deleteAllHistory()
            }
        }

        binding.btnFaq.setOnClickListener {
            startActivity(Intent(requireContext(), FAQActivity::class.java))
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
                    val profileImageUrl = profile.data?.profileImageURL
                    Log.d("Profile",profileImageUrl.toString())
                    Glide.with(this)
                        .load(profileImageUrl)
                        .into(binding.imgProfile)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("ProfileFragment", result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getProfile()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}