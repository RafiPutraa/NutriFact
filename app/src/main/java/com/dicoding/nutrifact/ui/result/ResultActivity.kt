package com.dicoding.nutrifact.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.ui.MainActivity
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.data.response.ProductData
import com.dicoding.nutrifact.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productData = intent.getSerializableExtra("PRODUCT_DATA") as? ProductData

        val barcodeId = productData?.barcodeId
        val merk = productData?.merk
        val varian = productData?.varian
        val sugar = productData?.sugar
        val fat = productData?.fat
        val healthGrade = productData?.healthGrade

        binding.tvProduct.text = merk
        binding.tvVariant.text = varian
        binding.tvSugar.text = "Sugar : ${sugar}g"
        binding.tvFat.text = "Fat : ${fat}g"
        when (healthGrade) {
            "A" -> {
                binding.tvDesc.text = getString(R.string.grade_a_information)
                Glide.with(this)
                    .load(R.drawable.grade_a)
                    .into(binding.imgGrade)
            }
            "B" -> {
                binding.tvDesc.text = getString(R.string.grade_b_information)
                Glide.with(this)
                    .load(R.drawable.grade_b)
                    .into(binding.imgGrade)
            }
            "C" -> {
                binding.tvDesc.text = getString(R.string.grade_c_information)
                Glide.with(this)
                    .load(R.drawable.grade_c)
                    .into(binding.imgGrade)
            }
            "D" -> {
                binding.tvDesc.text = getString(R.string.grade_d_information)
                Glide.with(this)
                    .load(R.drawable.grade_d)
                    .into(binding.imgGrade)
            }
            "E" -> {
                binding.tvDesc.text = getString(R.string.grade_e_information)
                Glide.with(this)
                    .load(R.drawable.grade_e)
                    .into(binding.imgGrade)
            }
        }

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}