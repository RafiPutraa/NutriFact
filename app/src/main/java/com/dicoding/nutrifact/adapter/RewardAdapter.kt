package com.dicoding.nutrifact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.data.response.PointData
import com.dicoding.nutrifact.databinding.RewardItemBinding
import com.dicoding.nutrifact.viewmodel.HomeViewModel

class RewardAdapter(
    private val dataPoint: List<PointData>,
    private val homeViewModel: HomeViewModel
) : RecyclerView.Adapter<RewardAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RewardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataPoint[position], homeViewModel)
    }

    override fun getItemCount(): Int = dataPoint.size

    class MyViewHolder(val binding: RewardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(award: PointData, viewModel: HomeViewModel) {
            binding.tvProductReward.text = award.name
            binding.tvRewardPoint.text = "${award.pointsRequired} Point"
            Glide.with(binding.root.context)
                .load(award.imageURL)
                .into(binding.imgProduct)

            binding.btnRedeem.setOnClickListener {
                if (award.awardId != null) {
                    viewModel.redeemAward(award.awardId)
                }
            }
        }
    }
}