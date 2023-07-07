package com.example.shortvideoapp

import android.content.ContentValues
import android.os.Build
import com.example.shortvideoapp.adapter.GridViewAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.shortvideoapp.adapter.VideoItemAdapter
import com.example.shortvideoapp.databinding.FragmentGridpostsBinding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.firebasefunctions.postFromMap
import com.example.shortvideoapp.model.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val UID = "uid"
private const val TYPE = "type"

/**
 * A simple [Fragment] subclass.
 * Use the [GridFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GridFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentGridpostsBinding
    private var uid: String? = null
    private var type: String? = null
    val postDataset= mutableListOf<Post>()
    lateinit var adapter:GridViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(UID)
            type = it.getString(TYPE)
        }
        binding = FragmentGridpostsBinding.inflate(layoutInflater)
        val view = binding.root


        var database = FirebaseDatabase.getInstance(databaseURL).reference
        if(type=="savedPosts")
        {
            database = database.child("$type/$uid")
        }else if(type=="posts"){
            database = database.child("users").child("$uid/$type")
        }
        val postKeys= mutableListOf<String>()
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                postDataset.clear()
                // Get Post object and use the values to update the UI
                for(snapshot in dataSnapshot.children) {
                    postKeys.add(snapshot.key as String)
                }
                for(postKey in postKeys){

                    val databaseRefPosts=FirebaseDatabase.getInstance(databaseURL).getReference("posts/$postKey")
                    databaseRefPosts.addValueEventListener(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            postDataset.clear()
                            // Get Post object and use the values to update the UI
                            val postMap = dataSnapshot.value as Map<String, Any?>
                            val post = postFromMap(postMap)
                            post.key = dataSnapshot.key as String

                            postDataset.add(post)
//                            Toast.makeText(context,"${postDataset.size}",Toast.LENGTH_SHORT).show()
                                // Data loading complete, set the adapter on the grid
                            adapter = GridViewAdapter(postDataset)
                            binding.gridPosts.adapter = adapter

                        }


                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                            Log.w(
                                ContentValues.TAG,
                                "loadPost:onCancelled",
                                databaseError.toException()
                            )
                        }
                    })
                }
                adapter = GridViewAdapter(postDataset)
                binding.gridPosts.adapter=adapter



            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGridpostsBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Toast.makeText(context,"${postDataset.size}",Toast.LENGTH_SHORT).show()


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param uid Parameter 1.
         * @param type Parameter 2.
         * @return A new instance of fragment GridFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun <T> newGridInstance(uid: String, type: String) =
            GridFragment().apply {
                arguments = Bundle().apply {
                    putString(UID, uid)
                    putString(TYPE, type)
                }
            }
    }
}