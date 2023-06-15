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
        var database = FirebaseDatabase.getInstance(databaseURL).getReference("posts")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                postDataset.clear()
                // Get Post object and use the values to update the UI
                for(snapshot in dataSnapshot.children) {
                    val postMap=snapshot.value as Map<String,Any?>
                    val post= postFromMap(postMap)
                    post.key=snapshot.key as String
                    postDataset.add(post)
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
        val view = inflater.inflate(R.layout.fragment_gridposts, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GridFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun <T> newGridInstance(param1: String, param2: String) =
            GridFragment().apply {
                arguments = Bundle().apply {
                    putString(UID, param1)
                    putString(TYPE, param2)
                }
            }
    }
}