package com.dicoding.nutrifact.adapter

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.databinding.FaqItemBinding
import com.dicoding.nutrifact.ui.faq.FAQActivity

class FAQAdapter(private val faqList: List<FAQActivity.FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(val binding: FaqItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = FaqItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        val binding = holder.binding

        binding.tvQuestion.text = faqItem.question
        binding.tvAnswer.text = faqItem.answer

        binding.tvAnswer.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE
        binding.divider.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE

        binding.ivArrow.setImageResource(
            if (faqItem.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        )

        binding.rootLayout.setOnClickListener {
            faqItem.isExpanded = !faqItem.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = faqList.size
}
