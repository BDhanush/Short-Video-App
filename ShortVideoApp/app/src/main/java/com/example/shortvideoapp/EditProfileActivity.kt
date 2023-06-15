package com.example.shortvideoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.shortvideoapp.databinding.ActivityEditProfileBinding
import com.example.shortvideoapp.databinding.ActivityProfileBinding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.userFromMap
import com.example.shortvideoapp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val auth=Firebase.auth
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("users/${auth.currentUser!!.uid}")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val userMap = dataSnapshot.value as Map<String, Any?>
                user = userFromMap(userMap)

                user.uid = auth.currentUser!!.uid
                binding.usernameInput.setText(user.username)
                binding.firstNameInput.setText(user.firstName)
                binding.lastNameInput.setText(user.lastName)
                binding.aboutInput.setText(user.about)
                Glide.with(view)
                    .load(user.profilePicture!!.toUri())
                    .into(binding.profilePicture)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}