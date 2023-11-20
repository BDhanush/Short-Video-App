package com.example.shortvideoapp

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView:SearchView=findViewById(R.id.searchView)
        val searchBar: SearchBar =findViewById(R.id.searchBar)
        searchView.setupWithSearchBar(searchBar)
        searchView.show()

    }
}