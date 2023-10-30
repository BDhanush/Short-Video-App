package com.example.shortvideoapp

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortvideoapp.adapter.CommentsAdapter
import com.example.shortvideoapp.databinding.ActivityCommentsBinding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    var auth: FirebaseAuth = Firebase.auth
    lateinit var adapter: CommentsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var postKey:String?=null;
    var commentDataset=mutableListOf<Comment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postKey=intent.getStringExtra("postKey");
        binding.commentsRecyclerView.setHasFixedSize(true)
        linearLayoutManager= LinearLayoutManager(this)
        binding.commentsRecyclerView.layoutManager=linearLayoutManager
        adapter = CommentsAdapter(applicationContext,commentDataset)
        adapter.notifyDataSetChanged()
        binding.commentsRecyclerView.adapter=adapter
        adapter.dataset=commentDataset
        readComments()

        binding.send.setOnClickListener {
            val body:String = binding.compose.text.toString().trim()

            if(body.isNotEmpty())
            {
                lockButton()
                sendComment(body)
            }
            binding.compose.text.clear()
        }
        binding.swipeRefreshLayout.setOnRefreshListener{
            refresh()
        }

//        binding.compose.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
//            binding.compose.isCursorVisible = hasFocus
//        }


    }
    private fun readComments()
    {
        var database = postKey?.let { FirebaseDatabase.getInstance(databaseURL).getReference("comments").child(it) };
        database?.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val newComments=mutableListOf<Comment>()
                commentDataset.clear()
                for(snapshot in dataSnapshot.children) {
                    val comment = snapshot.getValue(Comment::class.java)

                    comment?.commentKey=dataSnapshot.key
                    if (comment != null) {
                        commentDataset.add(comment);
//                        adapter.dataset.add(comment)
                    }
                }
                adapter.dataset=commentDataset
                adapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "Error", databaseError.toException())
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }
    private fun sendComment(body:String)
    {
        var comment=Comment(auth.currentUser!!.uid,body, Date())
        var database = postKey?.let { FirebaseDatabase.getInstance(databaseURL).getReference("comments").child(it) };
        val commentKey = database?.push()?.key

        if (commentKey != null) {
            database?.child(commentKey)?.setValue(comment)?.addOnSuccessListener {
                Toast.makeText(applicationContext,"Comment sent", Toast.LENGTH_SHORT).show()
                addComment(comment)
                unlockButton()
            }?.addOnFailureListener {
                Toast.makeText(applicationContext,it.message?:"Error", Toast.LENGTH_SHORT).show()
                unlockButton()
            }
        }

    }
    private fun refresh()
    {
        binding.commentsRecyclerView.layoutManager?.scrollToPosition(0)
        readComments()
    }


    private fun addComment(comment:Comment)
    {
        adapter.addComment(comment)
        binding.commentsRecyclerView.layoutManager?.scrollToPosition(adapter.dataset.size-1)
    }
    private fun lockButton()
    {
        binding.send.isEnabled=false
    }

    private fun unlockButton()
    {
        binding.send.isEnabled=true
    }
}