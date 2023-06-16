package com.example.shortvideoapp.signup
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.shortvideoapp.signup.SignupActivity3
import com.example.shortvideoapp.LoginActivity

import com.example.shortvideoapp.R
import com.example.shortvideoapp.databinding.ActivitySignup2Binding
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignupActivity2 : AppCompatActivity() {

    private lateinit var emailName: String
    private lateinit var userName: String
    private lateinit var binding: ActivitySignup2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignup2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val nextButton: Button = findViewById(R.id.nextButton)

        resetFields()
        nextButton.setOnClickListener {
            if(checkFields()) {
                next()
            }
        }
    }

    private fun next() {
        val firstname = intent.getStringExtra("firstname").toString()
        val lastname = intent.getStringExtra("lastname").toString()


        // Retrieve firstname and lastname values from TextInputEditTexts
        val usernameInput: TextInputEditText = findViewById(R.id.usernameInput)
        userName = usernameInput.text.toString().trim()
        val emailInput: TextInputEditText = findViewById(R.id.emailInput)
        emailName = emailInput.text.toString().trim()

        val intent = Intent(this, SignupActivity3::class.java)
        intent.putExtra("firstname", firstname)
        intent.putExtra("lastname", lastname)
        intent.putExtra("emailName", emailName.trim())
        intent.putExtra("userName", userName.trim())


        startActivity(intent)
    }

    private fun checkFields():Boolean
    {
        var check:Boolean=true;
        val p: Pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        val m: Matcher = p.matcher(binding.emailInput.text.toString().trim())
        val emailCheck: Boolean = m.matches()
        if(binding.usernameInput.text.toString().trim().isEmpty()){
        binding.usernameLayout.error = "This field is required"
        check = false
        }
        if (binding.emailInput.text.toString().trim().isEmpty()) {
            binding.emailLayout.error = "This field is required"
            check = false
        }

        else if (!emailCheck) {
            binding.emailLayout.error = "Enter correct email address"
            check = false
        }
        // after all validation return true.
        return check
    }

    private fun resetFields() {
        binding.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                if(binding.usernameInput.text.toString().trim().isEmpty()){
                    binding.usernameLayout.error = "This field is required"
                }
                else {
                    binding.usernameLayout.error = null
                }


            }
        })
        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                val p: Pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
                val m: Matcher = p.matcher(binding.emailInput.text.toString().trim())
                val emailCheck: Boolean = m.matches()

                if (binding.emailInput.text.toString().trim().isEmpty()) {
                    binding.emailLayout.error = "This field is required"
                } else if (!emailCheck) {
                    binding.emailLayout.error = "Enter correct email address"
                }
                else {
                    binding.emailLayout.error = null
                }
            }
        })



    }

}