package com.example.shortvideoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Video
import kotlinx.coroutines.NonCancellable.start

class VideoItemAdapter(private val context: Context, val dataset:MutableList<Video>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val seekBar:SeekBar = view.findViewById(R.id.seekbar)

        init{
            videoView.setOnPreparedListener{ mp ->
                val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                val screenRatio = videoView.width / videoView.height.toFloat()
                val scaleX = videoRatio / screenRatio
                if (scaleX >= 1f) {
                    videoView.scaleX = scaleX
                } else {
                    videoView.scaleY = 1f / scaleX
                }
                mp.start()
                mp.isLooping = true
            }
            var pauseCheck:Boolean = false;
            videoView.setOnClickListener{
                Toast.makeText(it.context, "$pauseCheck", Toast.LENGTH_SHORT).show();
                pauseCheck = if (!pauseCheck) {
                    videoView.pause()
                    true
                } else {
                    videoView.start()
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.videoView.setVideoPath(item.videoURL)
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount() = dataset.size
}