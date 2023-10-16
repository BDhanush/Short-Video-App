package com.example.shortvideoapp.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.R
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.model.Comment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat

class CommentsAdapter(context: Context, var dataset:MutableList<Comment>): RecyclerView.Adapter<CommentsAdapter.ItemViewHolder>()
{
    lateinit var context: Context

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username:TextView= view.findViewById(R.id.username)
        val body:TextView= view.findViewById(R.id.body)
        val date:TextView= view.findViewById(R.id.date)
        val time:TextView=view.findViewById(R.id.time)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        context=parent.context

        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        val databaseRefUser = FirebaseDatabase.getInstance(databaseURL).getReference("users/${item.uid}/username")
        databaseRefUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                holder.username.text = dataSnapshot.value.toString()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

        holder.body.text = item.body.toString()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.date.text= item.timestamp?.let { dateFormat.format(it) }
        val timeFormat = SimpleDateFormat("hh:mm a")
        holder.time.text= item.timestamp?.let { timeFormat.format(it) }



    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size

    fun addComment(comment: Comment)
    {
        dataset.add(comment)
        notifyItemInserted(dataset.size-1)
    }

}