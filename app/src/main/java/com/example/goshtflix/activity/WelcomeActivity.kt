package com.example.goshtflix.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goshtflix.databinding.ActivityWelcomeBinding


class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {

                navigateToLoginActivity()

        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}