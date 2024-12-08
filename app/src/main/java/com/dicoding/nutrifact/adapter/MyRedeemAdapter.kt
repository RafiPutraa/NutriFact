package com.dicoding.nutrifact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.data.response.RedeemData
import com.dicoding.nutrifact.databinding.RedeemItemBinding
import com.dicoding.nutrifact.util.withDateFormat

class MyRedeemAdapter(private val dataRedeem: List<RedeemData>
) : RecyclerView.Adapter<MyRedeemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RedeemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataRedeem[position])
    }

    override fun getItemCount(): Int = dataRedeem.size

    inner class MyViewHolder(val binding: RedeemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(redeem: RedeemData) {
            binding.tvProductReward.text = redeem.awardName
            binding.tvRewardPoint.text = "${redeem.pointsRedeemed} Point"
            binding.tvDateRedeem.text = redeem.redeemedAt?.withDateFormat() ?: ""
            Glide.with(binding.root.context)
                .load(redeem.imageURL)
                .into(binding.imgProduct)
        }
    }
}