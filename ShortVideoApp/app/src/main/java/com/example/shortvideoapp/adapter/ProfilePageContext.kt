package com.example.shortvideoapp.adapter

import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.shortvideoapp.AboutFragment
import com.example.shortvideoapp.GridFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun pageBasedOnContext(udi:String?,tabs:MutableList<Pair<String, Fragment>>,button:LinearLayout,buttonOther:LinearLayout):Unit
{
    val auth = Firebase.auth
    if(udi==(auth.currentUser!!.uid))
    {
        buttonOther.visibility = View.GONE
        button.visibility = View.VISIBLE
        tabs.clear()
        tabs.add(Pair("Posts", GridFragment()))
        tabs.add(Pair("Saved", AboutFragment()))
        tabs.add(Pair("About", AboutFragment()))
        return
    }

    button.visibility = View.GONE
    buttonOther.visibility = View.VISIBLE
    tabs.clear()
    tabs.add(Pair("Posts", GridFragment()))
    tabs.add(Pair("About", AboutFragment()))

}