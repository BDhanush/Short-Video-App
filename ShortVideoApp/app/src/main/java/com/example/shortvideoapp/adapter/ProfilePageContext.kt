package com.example.shortvideoapp.adapter

import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.shortvideoapp.AboutFragment
import com.example.shortvideoapp.AboutFragment.Companion.newAboutInstance
import com.example.shortvideoapp.GridFragment
import com.example.shortvideoapp.GridFragment.Companion.newGridInstance
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun pageBasedOnContext(uid:String,about:String,tabs:MutableList<Pair<String, Fragment>>,button:LinearLayout,buttonOther:LinearLayout):Unit
{
    val auth = Firebase.auth
    if(uid==(auth.currentUser!!.uid))
    {
        buttonOther.visibility = View.GONE
        button.visibility = View.VISIBLE
        tabs.clear()
        tabs.add(Pair("Posts", newGridInstance<GridFragment>(uid,"savedPosts")))
        tabs.add(Pair("Saved", newGridInstance<GridFragment>(uid,"posts")))
        tabs.add(Pair("About", newAboutInstance<AboutFragment>(about)))
        return
    }

    button.visibility = View.GONE
    buttonOther.visibility = View.VISIBLE
    tabs.clear()
    tabs.add(Pair("Posts", newGridInstance<GridFragment>(uid,"posts")))
    tabs.add(Pair("About", newAboutInstance<AboutFragment>(about)))

}
