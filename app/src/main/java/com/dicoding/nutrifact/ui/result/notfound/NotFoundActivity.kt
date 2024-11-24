package com.dicoding.nutrifact.ui.result.notfound

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.databinding.ActivityNotFoundBinding
import com.dicoding.nutrifact.ui.addnew.NewNutrition

class NotFoundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotFoundBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_not_found)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.buttonHome.setOnClickListener{
            finish()
        }
        binding.buttonAddNew.setOnClickListener{
            startActivity(Intent(this, NewNutrition::class.java))
        }




    }
}