package com.example.shortvideoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.*
import android.widget.*
import android.widget.SeekBar.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shortvideoapp.ProfilepageActivity
import com.example.shortvideoapp.R
import com.example.shortvideoapp.SignupActivity
import com.example.shortvideoapp.model.Post
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import org.w3c.dom.Text

class VideoItemAdapter(private val context: Context, val dataset:MutableList<Post>): RecyclerView.Adapter<VideoItemAdapter.ItemViewHolder>()
{
    private val database: DatabaseReference = FirebaseDatabase.getInstance("https://shortvideoapp-e7456-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    @SuppressLint("ClickableViewAccessibility")
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val seekBar:SeekBar = view.findViewById(R.id.seekbar)
        private val shimmerLoading: ShimmerFrameLayout = view.findViewById(R.id.shimmerVideo)
        private val loadedVideo: ConstraintLayout = view.findViewById(R.id.loadedVideo)
        val profileButton:Button = view.findViewById(R.id.homeButton)
        val videoTitle:TextView=view.findViewById(R.id.title)
        val videoDescription:TextView=view.findViewById(R.id.description)
        val username:TextView=view.findViewById(R.id.creatorName)
        val profilePicture:ImageView=view.findViewById(R.id.profilePicture)
        val saveButton:CheckBox=view.findViewById(R.id.saveButton)
        init{
            videoTitle.setOnClickListener {
                videoDescription.visibility= if(videoDescription.visibility == INVISIBLE) VISIBLE else INVISIBLE
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
    /**
     * Return the size of your dataset (invoked by the layout manager)
     */ override fun getItemCount() = dataset.size
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