package com.dicoding.nutrifact.ui.result.notfound

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.nutrifact.MainActivity
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.databinding.ActivityNotFoundBinding
import com.dicoding.nutrifact.ui.addnew.NewNutrition

class NotFoundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotFoundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotFoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val barcodeValue = intent.getStringExtra("BARCODE_VALUE")

        binding.buttonHome.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        binding.buttonAddNew.setOnClickListener {
            val intent = Intent(this, NewNutrition::class.java)
            intent.putExtra("BARCODE_VALUE", barcodeValue)
            startActivity(intent)
        }
    }
}