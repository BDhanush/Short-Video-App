package com.example.shortvideoapp

import android.app.ProgressDialog.show
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = Firebase.auth

        val email:EditText=findViewById(R.id.emailInput)
        val password:EditText=findViewById(R.id.passwordInput)
        val signupButton:Button=findViewById(R.id.signupButton)

        signupButton.setOnClickListener {


            if(email.text.toString()=="" || password.text.toString()=="")
            {
                return@setOnClickListener
            }

            val emailtext=email.text.toString()
            val passtext=password.text.toString()

            auth.createUserWithEmailAndPassword(emailtext, passtext).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "Authentication successful.",
                            Toast.LENGTH_SHORT
                        ).show()
                        email.setText("")
                        password.setText("")
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                        Toast.makeText(this, "Failed Registration: " + task.getException(), Toast.LENGTH_SHORT)
//                            .show()
                        Toast.makeText(
                            this, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}