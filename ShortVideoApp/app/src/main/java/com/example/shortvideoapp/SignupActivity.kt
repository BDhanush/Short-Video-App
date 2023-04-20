package com.example.shortvideoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupButton: Button = findViewById(R.id.signupButton)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            finish()
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
        signupButton.setOnClickListener {
            signup()
        }
    }

    private fun signup() {
        val email: EditText = findViewById(R.id.emailInput)
        val password: EditText = findViewById(R.id.passwordInput)
        val confirmPassword: EditText = findViewById(R.id.confirmPasswordInput)
        val username: EditText = findViewById(R.id.usernameInput)

        auth = Firebase.auth

        if (email.text == null || email.text.toString().isEmpty()) {
            Toast.makeText(
                this, "Please enter an email address.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (username.text.toString() == password.text.toString()) {
            Toast.makeText(
                this, "Username and password cannot be the same.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.text.toString() != confirmPassword.text.toString()) {
            Toast.makeText(
                this, "Passwords do not match.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.text.toString().length < 4) {
            Toast.makeText(
                this, "Password should be at least 4 characters long.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "Authentication successful.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorCode: String = (task.exception as FirebaseAuthException?)!!.errorCode;
                    Toast.makeText(
                        this, signupErrorToast(errorCode), Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    fun signupErrorToast(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_CUSTOM_TOKEN" -> "The custom token format is incorrect."
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> "The custom token corresponds to a different audience."
            "ERROR_INVALID_CREDENTIAL" -> "The supplied auth credential is malformed or has expired."
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_WRONG_PASSWORD" -> "The password is invalid or the user does not have a password."
            "ERROR_USER_MISMATCH" -> "The supplied credentials do not correspond to the previously signed in user."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "This operation is sensitive and requires recent authentication. Log in again before retrying this request."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This credential is already associated with a different user account."
            "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
            "ERROR_USER_TOKEN_EXPIRED" -> "The user's credential is no longer valid. The user must sign in again."
            "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier. The user may have been deleted."
            "ERROR_INVALID_USER_TOKEN" -> "The user's credential is no longer valid. The user must sign in again."
            "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed. You must enable this service in the console."
            "ERROR_WEAK_PASSWORD" -> "The given password is weak."
            "ERROR_MISSING_EMAIL" -> "An email address must be provided."
            else -> {
                ""
            }
        }.toString()
    }
}
