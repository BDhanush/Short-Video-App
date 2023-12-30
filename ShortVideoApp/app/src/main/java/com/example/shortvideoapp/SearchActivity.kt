package com.example.shortvideoapp

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var adapter:SearchAdapter
    lateinit var searchRecyclerView: RecyclerView
    var tag:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        tag=intent.getStringExtra("tag");

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView:SearchView=findViewById(R.id.searchView)
        val searchBar: SearchBar =findViewById(R.id.searchBar)
        searchView.setupWithSearchBar(searchBar)
        searchView.show()

        val list:MutableList<Post> = mutableListOf<Post>();
        val adapter= SearchAdapter(this,list);
        val layoutManager = GridLayoutManager(this,2);

        searchRecyclerView = binding.searchRecyclerView // Initialize searchRecyclerView
        searchRecyclerView.layoutManager = layoutManager
        searchRecyclerView.adapter = adapter

        binding.searchRecyclerView.setLayoutManager(layoutManager);
        binding.searchRecyclerView.setAdapter(adapter);
        readData()
        if(tag!=null)
        {
            searchView.setText(tag)
        }
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                filter(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })

        searchView.editText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            searchBar.text = searchView.text
            searchView.hide()
            false
        }


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
    private fun filter(searchPrefix: String) {
        val database = FirebaseDatabase.getInstance(databaseURL).getReference("posts")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val filteredList = mutableListOf<Post>()

                for (snapshot in dataSnapshot.children) {
                    val postMap = snapshot.value as Map<String, Any?>
                    val post = postFromMap(postMap)
                    post.key = snapshot.key as String

                    if (post.title?.contains(searchPrefix, ignoreCase = true) == true ||
                        post.tagsText?.contains(searchPrefix, ignoreCase = true) == true) {
                        filteredList.add(post)
                    } else {
                        val tags = post.tagsText?.split(",")?.map { it.trim() } ?: listOf()
                        for (tag in tags) {
                            if (tag.contains(searchPrefix, ignoreCase = true)) {
                                filteredList.add(post)
                                break
                            }
                        }
                    }
                }

                adapter = SearchAdapter(applicationContext, filteredList)
                searchRecyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SearchActivity", "Firebase search cancelled: ${databaseError.message}")
            }
        })
    }
}