package com.example.shortvideoapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.shortvideoapp.databinding.ActivityEditProfileBinding
import com.example.shortvideoapp.databinding.ActivityProfileBinding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.userFromMap
import com.example.shortvideoapp.model.User
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : AppCompatActivity() {
    lateinit var user: User
    val SELECT_IMAGE=200
    var selectedImageUri: Uri?=null
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val auth=Firebase.auth
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("users/${auth.currentUser!!.uid}")
        database.addValueEventListener(object : ValueEventListener {
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
        binding.editImage.setOnClickListener {
            addimage()
        }
        binding.submit.setOnClickListener {
            if(user.firstName!=binding.firstNameInput.text.toString() || user.lastName!=binding.lastNameInput.text.toString() || user.about!=binding.aboutInput.text.toString() || user.username!=binding.usernameInput.text.toString() || selectedImageUri!=null) {

                user.firstName=binding.firstNameInput.text.toString()
                user.lastName=binding.lastNameInput.text.toString()
                user.username=binding.usernameInput.text.toString()
                user.about=binding.aboutInput.text.toString()

                updateUser()
            }else {
                Toast.makeText(this, "No Updates", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun updateUser()
    {
        val submit:Button=findViewById(R.id.submit)
        val progressBarUpdate:CircularProgressIndicator=findViewById(R.id.progressBarUpdate)
        submit.isEnabled=false

        submit.text="Updating"
        progressBarUpdate.show()
        val database = FirebaseDatabase.getInstance(databaseURL).reference

        if(selectedImageUri!=null)
        {
            val storageRef=Firebase.storage

            val ref = storageRef.reference.child("images/${user.uid}/profilePicture")
            val uploadTask = ref.putFile(selectedImageUri!!)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    user.profilePicture=downloadUri.toString()
                    val updates=user.toMap()

                    database.child("users").child(user.uid!!).updateChildren(updates).addOnSuccessListener {
                        submit.isEnabled=true
                        submit.text="Update"
                        progressBarUpdate.hide()
                    }
                    Toast.makeText(this,"Profile Updated", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            val updates=user.toMap()

            database.child("users").child(user.uid!!).updateChildren(updates).addOnSuccessListener {
                submit.isEnabled=true
                submit.text="Update"
                progressBarUpdate.hide()
            }
            Toast.makeText(this,"Profile Updated", Toast.LENGTH_SHORT).show()
        }


    }

    private fun addimage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    @SuppressLint("Range")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode==SELECT_IMAGE)
        {
            selectedImageUri=data!!.data
            binding.profilePicture.setImageURI(selectedImageUri)
        }
    }

}














