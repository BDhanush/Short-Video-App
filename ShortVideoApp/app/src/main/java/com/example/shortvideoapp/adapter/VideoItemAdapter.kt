package com.example.shortvideoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Video
import com.facebook.shimmer.ShimmerFrameLayout


class VideoItemAdapter(private val context: Context, val dataset:MutableList<Video>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val seekBar:SeekBar = view.findViewById(R.id.seekbar)
        private val shimmerLoading: ShimmerFrameLayout = view.findViewById(R.id.shimmerVideo)
        private val loadedVideo: ConstraintLayout = view.findViewById(R.id.loadedVideo)
        init{

            val update: Runnable = object : Runnable {
                override fun run() {
                    seekBar.progress = videoView.currentPosition
                    seekBar.postDelayed(this, 1)
                }
            }

            videoView.setOnPreparedListener{ mp ->

                shimmerLoading.visibility= GONE
                loadedVideo.visibility= VISIBLE

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


                seekBar.max = videoView.duration
                seekBar.postDelayed(update, 1)
            }
            videoView.setOnClickListener{
                Toast.makeText(it.context, if (videoView.isPlaying) "pause" else "play", Toast.LENGTH_SHORT).show();
                if (videoView.isPlaying) {
                    videoView.pause()
                } else {
                    videoView.start()
                }
            }

            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        // this is when actually seekbar has been seeked to a new position
                        videoView.seekTo(progress)
                    }
                }
            })
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