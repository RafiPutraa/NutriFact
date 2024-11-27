package com.dicoding.nutrifact.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.local.entity.HistoryEntity
import com.dicoding.nutrifact.databinding.ProductItemBinding

class HistoryAdapter(private val historyList: List<HistoryEntity>,
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    class HistoryViewHolder(val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            val healthGrade = history.healthGrade

            binding.tvProductName.text = history.merk
            binding.tvVariantName.text = history.varian
            binding.tvSugar.text = "Sugar : ${history.sugar}g"
            binding.tvFat.text = "Fat : ${history.fat}g"

            when (healthGrade) {
                "A" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_a)
                        .into(binding.imgGradeLogo)
                }
                "B" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_b)
                        .into(binding.imgGradeLogo)
                }
                "C" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_c)
                        .into(binding.imgGradeLogo)
                }
                "D" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_d)
                        .into(binding.imgGradeLogo)
                }
                "E" -> {
                    Glide.with(binding.root.context)
                        .load(R.drawable.grade_e)
                        .into(binding.imgGradeLogo)
                }
            }
        }
    }
}