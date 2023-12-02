package com.example.shortvideoapp

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shortvideoapp.adapter.SearchAdapter
import com.example.shortvideoapp.databinding.ActivitySearchBinding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.postFromMap
import com.example.shortvideoapp.model.Post
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    val postDataset= mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView:SearchView=findViewById(R.id.searchView)
        val searchBar: SearchBar =findViewById(R.id.searchBar)
        searchView.setupWithSearchBar(searchBar)
        searchView.show()

        val list:MutableList<Post> = mutableListOf<Post>();
        val adapter= SearchAdapter(this,list);
        val layoutManager = GridLayoutManager(this,2);

        binding.searchRecyclerView.setLayoutManager(layoutManager);
        binding.searchRecyclerView.setAdapter(adapter);
        readData()

    }
    fun readData()
    {
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("posts")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postDataset.clear()
                // Get Post object and use the values to update the UI
                for(snapshot in dataSnapshot.children) {
                    val postMap=snapshot.value as Map<String,Any?>
                    val post= postFromMap(postMap)
                    post.key=snapshot.key as String
                    postDataset.add(post)
                }

                if (postDataset.isEmpty()) {
                    startActivity(Intent(applicationContext, AddVideoActivity::class.java))
                }

                val adapter = SearchAdapter(applicationContext,postDataset);
                binding.searchRecyclerView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}