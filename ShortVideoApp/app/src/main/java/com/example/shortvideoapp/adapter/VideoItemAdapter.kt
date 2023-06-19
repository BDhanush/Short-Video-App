package com.example.shortvideoapp.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.media.MediaPlayer
import android.view.*
import android.widget.*
import android.widget.SeekBar.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.shortvideoapp.MainActivity
import com.example.shortvideoapp.ProfilePageActivity
import com.danikula.videocache.HttpProxyCacheServer
import com.example.shortvideoapp.R
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.userFromMap
import com.example.shortvideoapp.model.Post
import com.example.shortvideoapp.model.User
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class VideoItemAdapter(private val context: Context, val dataset:MutableList<Post>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    var user:User?=null
    val auth = Firebase.auth
    val database = FirebaseDatabase.getInstance(databaseURL).reference

    @SuppressLint("ClickableViewAccessibility")
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val seekBar:SeekBar = view.findViewById(R.id.seekbar)
        val proxy: HttpProxyCacheServer = ProxyFactory.getProxy(context)
        private val mediaPlayer: MediaPlayer = MediaPlayer()
        private val shimmerLoading: ShimmerFrameLayout = view.findViewById(R.id.shimmerVideo)
        private val loadedVideo: ConstraintLayout = view.findViewById(R.id.loadedVideo)
        val profileButton:Button = view.findViewById(R.id.homeButton)
        val videoTitle:TextView=view.findViewById(R.id.title)
        val videoDescription:TextView=view.findViewById(R.id.description)
        val stats:ConstraintLayout=view.findViewById(R.id.descriptionAndCounts)
        val username:TextView=view.findViewById(R.id.creatorName)
        val profilePicture:ImageView=view.findViewById(R.id.profilePicture)
        val upvote:Button=view.findViewById(R.id.upvoteButton)
        val downvote:Button=view.findViewById(R.id.downvoteButton)
        val upvoteCount:TextView=view.findViewById(R.id.upvotes)
        val downvoteCount:TextView=view.findViewById(R.id.downvotes)
        val saveButton:CheckBox=view.findViewById(R.id.saveButton)
        val shareButton:Button=view.findViewById(R.id.shareButton)
        val progressBar:CircularProgressIndicator=view.findViewById(R.id.progressBar)


        init{
            videoTitle.setOnClickListener {
                stats.visibility= if(stats.visibility == GONE) {
                    videoTitle.maxLines= Int.MAX_VALUE
                    VISIBLE
                }else{
                    videoTitle.maxLines=2
                    GONE
                }
            }
            val update: Runnable = object : Runnable {
                override fun run() {
                    seekBar.progress = videoView.currentPosition
                    seekBar.postDelayed(this, 1)
                }
            }

            videoView.setOnPreparedListener{ mp ->

                shimmerLoading.visibility= GONE
                progressBar.show()
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
                progressBar.hide()
                mp.isLooping = true


                seekBar.max = videoView.duration
                seekBar.postDelayed(update, 1)
            }
            profileButton.setOnClickListener{
//                Toast.makeText(context, "Click", Toast.LENGTH_LONG).show()
                openProfile(context,auth.currentUser!!.uid)
            }
//            videoView.setOnErrorListener(MediaPlayer.OnErrorListener{
//                mp, what, extra ->
//                Toast.makeText(context, "Can't play video, try again later", Toast.LENGTH_LONG).show()
//                true
//            })
             val videoGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (videoView.isPlaying) {
                        videoView.pause()
                    } else {
                        videoView.start()
                    }
                    return super.onSingleTapConfirmed(e)
                }
            })
            videoView.setOnTouchListener { _, event -> videoGestureDetector.onTouchEvent(event) }


            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        // this is when actually seekbar has been sought to a new position
                        videoView.seekTo(progress)
                    }
                }
            })

        }
        fun preloadVideos(videoUrls: List<String>) {
            for (videoUrl in videoUrls) {
                mediaPlayer.apply {
                    reset()
                    val proxyUrl = proxy.getProxyUrl(videoUrl)
                    setDataSource(proxyUrl)
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        // Video is preloaded and ready to play
                        mp.isLooping = true
                        mp.seekTo(0)
                    }
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

        val PRELOAD_VIDEO_COUNT = 3;
        val nextVideos = dataset.subList(position + 1, minOf(position + 1 + PRELOAD_VIDEO_COUNT, dataset.size))
        val nextVideoUrls = nextVideos.map { it.videoURL }
        holder.preloadVideos(nextVideoUrls as List<String>)

        // Set the video path for VideoView
        val proxyUrl = holder.proxy.getProxyUrl(item.videoURL)
        holder.videoView.setVideoPath(proxyUrl)

        holder.videoTitle.text=item.title
        holder.videoDescription.text = item.description
        holder.upvote.setOnClickListener {
            database.child("upvotes/${item.key}").child(auth.currentUser!!.uid).setValue(true)
            database.child("downvotes/${item.key}").child(auth.currentUser!!.uid).removeValue()

        }
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                if(item.key!=null)
                    if(dataSnapshot.child("users/${auth.currentUser!!.uid}/savedPosts").child(item.key!!).exists()){
                        holder.saveButton.isChecked=true
                    }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        holder.downvote.setOnClickListener {
            database.child("downvotes/${item.key}").child(auth.currentUser!!.uid).setValue(true)
            database.child("upvotes/${item.key}").child(auth.currentUser!!.uid).removeValue()
        }
        val databaseRefUpvotes=database.child("upvotes/${item.key}")
        val databaseRefDownvotes=database.child("downvotes/${item.key}")
        databaseRefUpvotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                holder.upvoteCount.text=(dataSnapshot.childrenCount).toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        databaseRefDownvotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                holder.downvoteCount.text=(dataSnapshot.childrenCount).toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

        val databaseRefUser=database.child("users")
        databaseRefUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                holder.username.text=(dataSnapshot.child(item.uid!!).child("username").value).toString()
                val profilePicture=(dataSnapshot.child(item.uid!!).child("profilePicture").value).toString()
                if(profilePicture!="")
                    Glide.with(context).load(profilePicture.toUri()).into(holder.profilePicture);

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        holder.saveButton.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked){
                databaseRefUser.child("${auth.currentUser!!.uid}/savedPosts").child(item.key!!).setValue(true)
            }else{
                databaseRefUser.child("${auth.currentUser!!.uid}/savedPosts").child(item.key!!).removeValue()

            }
        }
        holder.profilePicture.setOnClickListener {
            openProfile(context,item.uid!!)
        }
        holder.username.setOnClickListener {
            openProfile(context,item.uid!!)
        }
        holder.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "app://shortform.open/post/${item.key}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null).also {
                it.addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        }

    }
    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size
}

fun openProfile(context:Context,uid:String){
    Intent(context, ProfilePageActivity::class.java).also{
        it.addFlags(FLAG_ACTIVITY_NEW_TASK)
        it.putExtra("uid",uid)
        context.startActivity(it)
    }
}