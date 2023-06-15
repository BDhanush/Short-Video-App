package com.example.shortvideoapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.shortvideoapp.DisplayVideoActivity
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Post

class GridViewAdapter(private val dataset: MutableList<Post>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): Any {
        return dataset[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            // Inflate the grid_item layout
            view = LayoutInflater.from(parent?.context).inflate(R.layout.grid_item, parent, false)

            // Create a ViewHolder to hold references to the views
            viewHolder = ViewHolder()
            viewHolder.thumbnailImageView = view.findViewById<ImageView>(R.id.imageView)

            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val post = dataset[position]

        // Load the video thumbnail image using Glide library
        parent?.context?.let {
            Glide.with(it)
                .load(post.thumbnail)
                .into(viewHolder.thumbnailImageView)
        }

        // Set click listener for the thumbnail image
        viewHolder.thumbnailImageView.setOnClickListener {
            // Handle the click event
            val intent = Intent(parent?.context, DisplayVideoActivity::class.java)
            intent.putExtra("videoUrl", post.videoURL)
            // Add any other necessary data to the intent
            parent?.context?.startActivity(intent)
        }

        return view
    }

    // ViewHolder class to hold references to the views
    private class ViewHolder {
        lateinit var thumbnailImageView: ImageView
    }
}


