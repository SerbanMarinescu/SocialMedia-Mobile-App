package com.example.proiect_licenta_2023.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Adapter.PostAdapter
import com.example.proiect_licenta_2023.Model.Post
import com.example.proiect_licenta_2023.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PostDetailsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var postAdapter: PostAdapter?=null
    private var postList:MutableList<Post>?=null
    private var postId: String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_post_details, container, false)

        val preferences=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)

        if(preferences!=null){
            this.postId=preferences.getString("postId","none")
        }

        recyclerView=view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager=LinearLayoutManager(context)
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()
        postAdapter=context?.let{ PostAdapter(it,postList as ArrayList<Post>)}
        recyclerView.adapter =postAdapter


        retrieveAllPosts()

        return view
    }

    private fun retrieveAllPosts() {
        val postsRef= FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!)

        postsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                val post=snapshot.getValue(Post::class.java)
                postList!!.add(post!!)

                postAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}