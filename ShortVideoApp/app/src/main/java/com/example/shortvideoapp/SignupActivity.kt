package com.example.shortvideoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shortvideoapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupButton:Button=findViewById(R.id.signupButton)
        val loginButton:Button=findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            finish()
            Intent(this,LoginActivity::class.java).also{
                startActivity(it)
            }
        }
        signupButton.setOnClickListener {
            signup()
        }
    }
    private fun signup()
    {
        val email:EditText=findViewById(R.id.emailInput)
        val password:EditText=findViewById(R.id.passwordInput)
        val username:EditText=findViewById(R.id.usernameInput)


        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this) {

            if (it.isSuccessful) {
                Toast.makeText(
                    this, "Authentication successful.",
                    Toast.LENGTH_SHORT
                ).show()

                val timestamp = ""+System.currentTimeMillis()
                writeNewUser(timestamp,username.toString(),"default","default",email.toString());

                finish()

            } else {
                Toast.makeText(
                    this, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun writeNewUser(uid:String,username:String,firstName:String,lastName:String,email:String) {
        Toast.makeText(
            this, "function called",
            Toast.LENGTH_SHORT
        ).show()

        //put details into Database
        val user = User(uid,username,firstName,lastName,email);
        val dbReference = FirebaseDatabase.getInstance().getReference("users")
        dbReference.child(uid).setValue(Collections.singletonList(user))
            .addOnSuccessListener {
                //user details added successfully
                Toast.makeText(
                    this, "data pushed",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                //failed adding user details
                Toast.makeText(
                    this, e.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

