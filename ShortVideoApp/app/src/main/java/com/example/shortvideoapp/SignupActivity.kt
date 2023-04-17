package com.example.shortvideoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

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
    private fun signup() {
        val email:EditText = findViewById(R.id.emailInput)
        val password:EditText = findViewById(R.id.passwordInput)
        val confirmPassword:EditText = findViewById(R.id.confirmPasswordInput)
        val username:EditText = findViewById(R.id.usernameInput)

        auth = Firebase.auth

        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(
                this, "Passwords do not match.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this, "Authentication successful.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        this, AuthExceptionHandler.generateExceptionMessage(AuthExceptionHandler.AuthResultStatus.invalidEmail),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: FirebaseAuthInvalidUserException) {
                    Toast.makeText(
                        this, AuthExceptionHandler.generateExceptionMessage(AuthExceptionHandler.AuthResultStatus.userNotFound),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this, AuthExceptionHandler.generateExceptionMessage(AuthExceptionHandler.AuthResultStatus.undefined),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

class AuthExceptionHandler {
    enum class AuthResultStatus {
        invalidEmail,
        wrongPassword,
        userNotFound,
        userDisabled,
        tooManyRequests,
        operationNotAllowed,
        emailAlreadyExists,
        undefined
    }

    companion object {
        fun handleException(e: Exception): AuthResultStatus {
            return when (e) {
                is FirebaseAuthInvalidCredentialsException -> AuthResultStatus.invalidEmail
                is FirebaseAuthInvalidUserException -> AuthResultStatus.userNotFound
                else -> AuthResultStatus.undefined
            }
        }

        fun generateExceptionMessage(exceptionCode: AuthResultStatus): String {
            return when (exceptionCode) {
                AuthResultStatus.invalidEmail -> "Your email address appears to be malformed."
                AuthResultStatus.userNotFound -> "User with this email doesn't exist."
                else -> "An undefined Error happened."
            }
        }
    }
}