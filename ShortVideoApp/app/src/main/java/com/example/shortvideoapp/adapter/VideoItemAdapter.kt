package com.example.shortvideoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import android.widget.SeekBar.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.danikula.videocache.HttpProxyCacheServer
import com.example.shortvideoapp.ProfilePage
import com.example.shortvideoapp.R
import com.example.shortvideoapp.model.Post
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.firebase.database.*


class VideoItemAdapter(private val context: Context, val dataset:MutableList<Post>, var videoPreparedListener: OnVideoPreparedListener): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    @SuppressLint("ClickableViewAccessibility")
    inner class ItemViewHolder(private val view: View, var videoPreparedListener: OnVideoPreparedListener) : RecyclerView.ViewHolder(view) {
        val playerView: PlayerView = view.findViewById(R.id.playerView)
        val seekBar: SeekBar = view.findViewById(R.id.seekbar)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15000,
                60000,
                2500,
               5000
            )
            .build()
        val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).setLoadControl(loadControl).build()
        private val shimmerLoading: ShimmerFrameLayout = view.findViewById(R.id.shimmerVideo)
        private val loadedVideo: ConstraintLayout = view.findViewById(R.id.loadedVideo)
        val profileButton: Button = view.findViewById(R.id.homeButton)
        val videoTitle: TextView = view.findViewById(R.id.title)
        val videoDescription: TextView = view.findViewById(R.id.description)
        val username: TextView = view.findViewById(R.id.creatorName)
        val profilePicture: ImageView = view.findViewById(R.id.profilePicture)
        val saveButton: CheckBox = view.findViewById(R.id.saveButton)

        fun setVideoPath (url: String) {
            playerView.player = exoPlayer
            playerView.useController = false

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))

            videoTitle.setOnClickListener {
                videoDescription.visibility = if (videoDescription.visibility == View.INVISIBLE) View.VISIBLE else View.INVISIBLE
            }

            val update: Runnable = object : Runnable {
                override fun run() {
                    seekBar.progress = exoPlayer.currentPosition.toInt()
                    seekBar.postDelayed(this, 1)
                }
            }

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_READY && playWhenReady) {
                        shimmerLoading.visibility = View.GONE
                        loadedVideo.visibility = View.VISIBLE

                        seekBar.max = exoPlayer.duration.toInt()
                        seekBar.postDelayed(update, 1)
                    }
                }
            })

            profileButton.setOnClickListener {
                Intent(context, ProfilePage::class.java).also {
                    context.startActivity(it)
                }
            }

            val videoGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    exoPlayer.playWhenReady = !exoPlayer.isPlaying
                    return super.onSingleTapConfirmed(e)
                }
            })
            playerView.setOnTouchListener { _, event -> videoGestureDetector.onTouchEvent(event) }

            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        // This is when the seek bar is sought to a new position
                        exoPlayer.seekTo(progress.toLong())
                    }
                }
            })
        }
        fun preloadVideos(videoUrls: List<String>) {
            for (videoUrl in videoUrls) {
                val proxy: HttpProxyCacheServer = ProxyFactory.getProxy(context)
                val proxyUrl = proxy.getProxyUrl(videoUrl)
                val dataSourceFactory = DefaultDataSource.Factory(context)
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(proxyUrl)))
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.addListener(object : Player.Listener {
                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        if (!isLoading) {
                            exoPlayer.seekTo(0)
                            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                        }
                    }
                })
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)

        return ItemViewHolder(adapterLayout, videoPreparedListener)
    }

    object ProxyFactory {
        private var sharedProxy: HttpProxyCacheServer? = null
        fun getProxy(context: Context): HttpProxyCacheServer {
            return if (sharedProxy == null) newProxy(context).also {
                sharedProxy = it
            } else sharedProxy!!
        }

        private fun newProxy(context: Context): HttpProxyCacheServer {
            return HttpProxyCacheServer(context)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.videoTitle.text = item.title
        holder.videoDescription.text = item.description

        val PRELOAD_VIDEO_COUNT = 3;
        val nextVideos = dataset.subList(position + 1, minOf(position + 1 + PRELOAD_VIDEO_COUNT, dataset.size))
        val nextVideoUrls = nextVideos.map { it.videoURL }
        holder.preloadVideos(nextVideoUrls)

        // Set the video path for VideoView
        val proxy: HttpProxyCacheServer = ProxyFactory.getProxy(context)
        val proxyUrl = proxy.getProxyUrl(item.videoURL)
        holder.setVideoPath(proxyUrl)

        holder.saveButton.setOnClickListener {
//            if(holder.saveButton.isChecked)
//            {
//                database.child("users").child("saved").push(item.postURL)
//            }else{
//
//            }
        }
//        holder.username.text= getUsername(item.uid);
//        holder.profilePicture.setImageURI(Uri.parse(getProfilePicture(item.uid)));

    }
    override fun getItemCount() = dataset.size

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }
}

fun getUsername(uid:String):String{
    val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(uid).child("username");
    var username:String?=null;
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            username = dataSnapshot.value as String;
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }
    databaseUsername.addValueEventListener(postListener)
    return username!!
}

fun getProfilePicture(uid:String):String{
    val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("users").child(uid).child("profilePicture");
    var profilePicture:String?=null;
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            profilePicture = dataSnapshot.value as String;
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }
    databaseUsername.addValueEventListener(postListener)
    return profilePicture!!
}