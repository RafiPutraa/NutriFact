package com.dicoding.nutrifact.ui.result

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.dicoding.nutrifact.MainActivity
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
                Glide.with(this)
                    .load(R.drawable.grade_a)
                    .into(binding.imgGrade)
            }
            "B" -> {
                Glide.with(this)
                    .load(R.drawable.grade_b)
                    .into(binding.imgGrade)
            }
            "C" -> {
                Glide.with(this)
                    .load(R.drawable.grade_c)
                    .into(binding.imgGrade)
            }
            "D" -> {
                Glide.with(this)
                    .load(R.drawable.grade_d)
                    .into(binding.imgGrade)
            }
            "E" -> {
                Glide.with(this)
                    .load(R.drawable.grade_e)
                    .into(binding.imgGrade)
            }
        }

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}