package com.example.shortvideoapp.adapter

import android.os.Bundle
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Video

class VideoItemAdapter(private val context: Context, val dataset:MutableList<Video>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just an Affirmation object.
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.videoView.setVideoPath(item.videoURL)

//        val mediaController = MediaController(context)
//        mediaController.visibility = View.GONE;
//        // on below line we are setting anchor
//        // view for our media controller.
//        mediaController.setAnchorView(holder.videoView)
//
//        // on below line we are setting media player
//        // for our media controller.
//        mediaController.setMediaPlayer(holder.videoView)
//
//        // on below line we are setting media
//        // controller for our video view.
//        holder.videoView.setMediaController(mediaController)
//
//        // on below line we are
//        // simply starting our video view.
//        holder.videoView.start()
        holder.videoView.setOnPreparedListener{ mp ->
            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
            val screenRatio = holder.videoView.width / holder.videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                holder.videoView.scaleX = scaleX
            } else {
                holder.videoView.scaleY = 1f / scaleX
            }
            mp.start()
            mp.isLooping = true
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size
}