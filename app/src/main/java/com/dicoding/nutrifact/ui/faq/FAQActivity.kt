package com.dicoding.nutrifact.ui.faq

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.adapter.FAQAdapter
import com.dicoding.nutrifact.databinding.ActivityFaqactivityBinding

class FAQActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFaqactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val faqList = listOf(
            FAQItem(getString(R.string.question1), getString(R.string.answer1)),
            FAQItem(getString(R.string.question2), getString(R.string.answer2)),
            FAQItem(getString(R.string.question3), getString(R.string.answer3))
        )

        val faqAdapter = FAQAdapter(faqList)

        binding.rvFAQ.layoutManager = LinearLayoutManager(this)
        binding.rvFAQ.adapter = faqAdapter
        binding.btnBack.setOnClickListener { finish() }
    }

    data class FAQItem(
        val question: String,
        val answer: String,
        var isExpanded: Boolean = false
    )
}