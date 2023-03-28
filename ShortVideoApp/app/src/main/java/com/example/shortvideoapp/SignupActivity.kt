package com.example.shortvideoapp

import LoginActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        val loginBack: MaterialButton = findViewById(R.id.have_account_text)
        loginBack.setOnClickListener{
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}