package com.dicoding.nutrifact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.local.entity.HistoryEntity
import com.dicoding.nutrifact.databinding.HistoryCarouselBinding

class CarouselAdapter(private val historyList: List<HistoryEntity>) :
    RecyclerView.Adapter<CarouselAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    class MyViewHolder(val binding: HistoryCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            val healthGrade = history.healthGrade

            binding.tvProduct.text = history.merk
            binding.tvVariant.text = history.varian

            when (healthGrade) {
                "A" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_a)
                        .into(binding.imgGrade)
                }
                "B" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_b)
                        .into(binding.imgGrade)
                }
                "C" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_c)
                        .into(binding.imgGrade)
                }
                "D" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_d)
                        .into(binding.imgGrade)
                }
                "E" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_e)
                        .into(binding.imgGrade)
                }
            }
        }
    }
}