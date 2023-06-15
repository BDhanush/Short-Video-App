package com.example.shortvideoapp.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.SeekBar.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.shortvideoapp.MainActivity
import com.example.shortvideoapp.ProfilepageActivity
import com.example.shortvideoapp.R
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.userFromMap
import com.example.shortvideoapp.model.Post
import com.example.shortvideoapp.model.User
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class VideoItemAdapter(private val context: MainActivity, val dataset:MutableList<Post>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    var user:User?=null
    val auth = Firebase.auth
    val database = FirebaseDatabase.getInstance(databaseURL).reference

    @SuppressLint("ClickableViewAccessibility")
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val seekBar:SeekBar = view.findViewById(R.id.seekbar)
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
            profileButton.setOnClickListener{
//                Toast.makeText(context, "Click", Toast.LENGTH_LONG).show()
                Intent(context, ProfilepageActivity::class.java).also{
                    it.putExtra("uid",auth.currentUser!!.uid)
                    context.startActivity(it)
                }
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.videoTitle.text=item.title
        holder.videoView.setVideoPath(item.videoURL)
        holder.videoDescription.text = item.description
        holder.upvote.setOnClickListener {
            database.child("posts/${item.key}/upvotes").child(auth.currentUser!!.uid).setValue(true)
            database.child("posts/${item.key}/downvotes").child(auth.currentUser!!.uid).removeValue()

        }
        database.addListenerForSingleValueEvent(object : ValueEventListener {
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
            database.child("posts/${item.key}/downvotes").child(auth.currentUser!!.uid).setValue(true)
            database.child("posts/${item.key}/upvotes").child(auth.currentUser!!.uid).removeValue()
        }
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                holder.upvoteCount.text=(dataSnapshot.child("posts/${item.key}/upvotes").childrenCount).toString()
                holder.downvoteCount.text=(dataSnapshot.child("posts/${item.key}/downvotes").childrenCount).toString()
                holder.username.text=(dataSnapshot.child("users").child(item.uid!!).child("username").value).toString()
                val profilePicture=(dataSnapshot.child("users").child(item.uid!!).child("profilePicture").value).toString()
                if(profilePicture!="")
                    Glide.with(holder.itemView.context).load(profilePicture.toUri()).into(holder.profilePicture);

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
        holder.saveButton.setOnCheckedChangeListener { checkBox, isChecked ->
            if(isChecked){
                database.child("users/${auth.currentUser!!.uid}/savedPosts").child(item.key!!).setValue(true)
            }else{
                database.child("users/${auth.currentUser!!.uid}/savedPosts").child(item.key!!).removeValue()

            }
        }
        holder.profilePicture.setOnClickListener {
            Intent(context, ProfilepageActivity::class.java).also{
                it.putExtra("uid",item.uid)
                context.startActivity(it)
            }
        }

    }
    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size
}

fun getUsername(uid:String):String{
    val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance(databaseURL).reference.child("users").child(uid).child("username");
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
    val databaseUsername: DatabaseReference = FirebaseDatabase.getInstance(databaseURL).reference.child("users").child(uid).child("profilePicture");
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