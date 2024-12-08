package com.dicoding.nutrifact.ui.myredeem

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.adapter.MyRedeemAdapter
import com.dicoding.nutrifact.adapter.RewardAdapter
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.databinding.ActivityMyRedeemBinding
import com.dicoding.nutrifact.viewmodel.HomeViewModel
import com.dicoding.nutrifact.viewmodel.MyRedeemViewModel
import com.dicoding.nutrifact.viewmodel.ViewModelFactory

class MyRedeemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyRedeemBinding
    private lateinit var myRedeemAdapter: MyRedeemAdapter
    private val myRedeemViewModel: MyRedeemViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRedeemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myRedeemAdapter = MyRedeemAdapter(emptyList())
        binding.rvMyRedeem.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMyRedeem.adapter = myRedeemAdapter
        binding.btnBack.setOnClickListener {
            finish()
        }

        observeRedeem()
        myRedeemViewModel.getRedeemHistory()
    }

    private fun observeRedeem() {
        myRedeemViewModel.redeemHistoryResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    val redeemData = result.data
                    if (redeemData.data.isNullOrEmpty()) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.rvMyRedeem.visibility = View.GONE
                    } else {
                        binding.tvError.visibility = View.GONE
                        binding.rvMyRedeem.visibility = View.VISIBLE
                        Log.d("MyRedeemActivity", "Redeem Data: $redeemData")
                        myRedeemAdapter = MyRedeemAdapter(redeemData.data)
                        binding.rvMyRedeem.adapter = myRedeemAdapter
                    }
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("MyRedeemActivity", result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}