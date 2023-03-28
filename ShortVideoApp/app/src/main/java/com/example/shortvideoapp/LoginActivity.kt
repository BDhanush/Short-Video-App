package com.example.shortvideoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupButton:Button=findViewById(R.id.signupButton)
        signupButton.setOnClickListener{
            Intent(this,SignupActivity::class.java).also{
                startActivity(it)
            }
        }

    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}