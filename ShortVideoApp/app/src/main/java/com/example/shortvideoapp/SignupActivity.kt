package com.example.shortvideoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
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

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this) {

            if (it.isSuccessful) {
                val timestamp = ""+System.currentTimeMillis()

                //user details
                val hashMap = HashMap<String, Any>()
                hashMap["id"] = timestamp
                hashMap["email"] = "${email.text}"
                hashMap["password"] = "${password.text}"
                hashMap["timestamp"] = timestamp

                //put details into Database
                val dbReference = FirebaseDatabase.getInstance().getReference("Users")
                dbReference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener { taskSnapshot ->
                        //user details added successfully
                        Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        //failed adding user details
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}