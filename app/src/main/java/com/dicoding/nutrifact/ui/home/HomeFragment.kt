package com.dicoding.nutrifact.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import androidx.lifecycle.Observer
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import com.dicoding.nutrifact.data.local.HistoryRepository
import com.dicoding.nutrifact.data.local.room.HistoryDatabase
import com.dicoding.nutrifact.databinding.FragmentHomeBinding
import com.dicoding.nutrifact.ui.adapter.CarouselAdapter
import com.dicoding.nutrifact.ui.history.HistoryActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyRepository: HistoryRepository
    private lateinit var carouselAdapter: CarouselAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val spannable = SpannableString(binding.tvViewAll.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvViewAll.text = spannable
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(requireContext(),HistoryActivity::class.java))
        }

        val historyDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        historyRepository = HistoryRepository(historyDao)
        carouselAdapter = CarouselAdapter(emptyList())


        binding.vpCarousel.adapter = carouselAdapter
        historyRepository.getLastTenHistory().observe(viewLifecycleOwner, Observer { historyList ->
            if (historyList.isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.vpCarousel.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
                binding.vpCarousel.visibility = View.VISIBLE
                carouselAdapter = CarouselAdapter(historyList)
                binding.vpCarousel.adapter = carouselAdapter
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}