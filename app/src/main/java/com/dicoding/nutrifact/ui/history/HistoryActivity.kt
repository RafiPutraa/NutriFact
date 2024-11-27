package com.dicoding.nutrifact.ui.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.dicoding.nutrifact.data.local.HistoryRepository
import com.dicoding.nutrifact.data.local.room.HistoryDatabase
import com.dicoding.nutrifact.databinding.ActivityHistoryBinding
import com.dicoding.nutrifact.ui.adapter.HistoryAdapter
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHistoryBinding
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val historyDao = HistoryDatabase.getInstance(this).historyDao()
        historyRepository = HistoryRepository(historyDao)
        historyAdapter = HistoryAdapter(emptyList())

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter

        historyRepository.getAllHistory().observe(this, Observer { historyList ->
            if (historyList.isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.btnClear.visibility = View.GONE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
                binding.btnClear.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.VISIBLE
                historyAdapter = HistoryAdapter(historyList)
                binding.rvHistory.adapter = historyAdapter
            }
        })

        binding.btnClear.setOnClickListener {
            lifecycleScope.launch {
                historyRepository.deleteAllHistory()
            }
        }
    }
}