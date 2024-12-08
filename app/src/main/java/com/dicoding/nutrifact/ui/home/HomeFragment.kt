package com.dicoding.nutrifact.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import androidx.lifecycle.Observer
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.nutrifact.adapter.RewardAdapter
import com.dicoding.nutrifact.data.local.HistoryRepository
import com.dicoding.nutrifact.data.local.room.HistoryDatabase
import com.dicoding.nutrifact.databinding.FragmentHomeBinding
import com.dicoding.nutrifact.adapter.CarouselAdapter
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.ui.history.HistoryActivity
import com.dicoding.nutrifact.ui.login.LoginActivity
import com.dicoding.nutrifact.ui.myredeem.MyRedeemActivity
import com.dicoding.nutrifact.viewmodel.HomeViewModel
import com.dicoding.nutrifact.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyRepository: HistoryRepository
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var rewardAdapter: RewardAdapter
    private var loadingDialog: SweetAlertDialog? = null
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val spannable_redeem = SpannableString(binding.tvMyRedeem.text)
        spannable_redeem.setSpan(UnderlineSpan(), 0, spannable_redeem.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvMyRedeem.text = spannable_redeem
        binding.tvMyRedeem.setOnClickListener {
            startActivity(Intent(requireContext(),MyRedeemActivity::class.java))
        }

        val spannable = SpannableString(binding.tvViewAll.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvViewAll.text = spannable
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(),HistoryActivity::class.java))
        }

        val historyDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        historyRepository = HistoryRepository(historyDao)
        carouselAdapter = CarouselAdapter(emptyList())
        rewardAdapter = RewardAdapter(emptyList(), homeViewModel)


        binding.rvCarousel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCarousel.adapter = carouselAdapter

        binding.rvReward.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvReward.adapter = rewardAdapter

        fetchData()
        observeProfile()
        observeAwards()
        observeRedeem()
        homeViewModel.getProfile()
        homeViewModel.getAward()

        return root
    }

    private fun observeRedeem() {
        homeViewModel.redeemAwardResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Redeem Successful")
                        .setContentText("Congratulations! You've successfully redeemed your points.")
                        .setConfirmText("OK")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            homeViewModel.getProfile()
                        }
                        .show()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Redeem failed")
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
        homeViewModel.profileResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showProgBar(true)
                }
                is ResultState.Success -> {
                    showProgBar(false)
                    val profile = result.data
                    binding.tvPoint.text = profile.data?.points.toString()
                }
                is ResultState.Error -> {
                    showProgBar(false)
                    Log.e("HomeFragment", result.error)
                }
            }
        }
    }

    private fun observeAwards() {
        homeViewModel.awardResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showProgBar(true)
                }
                is ResultState.Success -> {
                    showProgBar(false)
                    val awardData = result.data
                    rewardAdapter = RewardAdapter(awardData.data ?: emptyList(), homeViewModel)
                    binding.rvReward.adapter = rewardAdapter
                }
                is ResultState.Error -> {
                    showProgBar(false)
                    Log.e("HomeFragment", result.error)
                }
            }
        }
    }

    private fun fetchData(){
        historyRepository.getLastTenHistory().observe(viewLifecycleOwner, Observer { historyList ->
            if (historyList.isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.rvCarousel.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
                binding.rvCarousel.visibility = View.VISIBLE
                carouselAdapter = CarouselAdapter(historyList)
                binding.rvCarousel.adapter = carouselAdapter
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            if (loadingDialog == null || !loadingDialog!!.isShowing) {
                loadingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE).apply {
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

    private fun showProgBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        fetchData()
        homeViewModel.getProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}