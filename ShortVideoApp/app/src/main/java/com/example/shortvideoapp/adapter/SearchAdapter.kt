package com.example.shortvideoapp.adapter

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Post

class SearchAdapter(context: Context, var dataset:MutableList<Post>): RecyclerView.Adapter<SearchAdapter.ItemViewHolder>()
{
    lateinit var context: Context

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail:ImageView=view.findViewById(R.id.thumbnail)
        val title:TextView=view.findViewById(R.id.title)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        context=parent.context

        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        Glide.with(context).load(item.thumbnail).into(holder.thumbnail)
        holder.title.text=item.title


    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size

}