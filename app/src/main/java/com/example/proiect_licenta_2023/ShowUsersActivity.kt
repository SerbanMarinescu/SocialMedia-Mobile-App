package com.example.proiect_licenta_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Adapter.MyImagesAdapter
import com.example.proiect_licenta_2023.Adapter.UserAdapter
import com.example.proiect_licenta_2023.Model.Post
import com.example.proiect_licenta_2023.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class ShowUsersActivity : AppCompatActivity() {

    private var id: String=""
    private var title: String=""

    private var userAdapter: UserAdapter?=null
    private var userList:List<User>?=null
    private var idList:List<String>?=null

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        val intent=intent

        id=intent.getStringExtra("id").toString()
        title=intent.getStringExtra("title").toString()

        toolbar=findViewById(R.id.toolbar)
        recyclerView=findViewById(R.id.recycle_view)

//        setSupportActionBar(toolbar)
          supportActionBar!!.title=title
          supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=linearLayoutManager
        userList=ArrayList()
        userAdapter= UserAdapter(this, userList as ArrayList<User>, false)
        recyclerView.adapter=userAdapter

        idList=ArrayList()

        when(title){
            "likes" -> getLikes()
            "following" -> getFollowing()
            "followers" -> getFollowers()
            "views" -> getViews()
        }


    }


    private fun getViews() {
        val ref= FirebaseDatabase.getInstance().reference.child("Story")
            .child(id).child(intent.getStringExtra("storyid").toString())
            .child("views")


        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for(snap in snapshot.children){
                    (idList as ArrayList<String>).add(snap.key!!)
                }

                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun getFollowers() {
        val followersRef= FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id)
            .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for(snap in snapshot.children){
                    (idList as ArrayList<String>).add(snap.key!!)
                }

                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun getFollowing() {
        val followersRef= FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (idList as ArrayList<String>).clear()

                for(snap in snapshot.children){
                    (idList as ArrayList<String>).add(snap.key!!)
                }

                showUsers()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun getLikes() {
        val likesRef= FirebaseDatabase.getInstance().reference.child("Likes").child(id)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (idList as ArrayList<String>).clear()

                    for(snap in snapshot.children){
                        (idList as ArrayList<String>).add(snap.key!!)
                    }

                    showUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showUsers() {
        val userRef=FirebaseDatabase.getInstance().reference.child("Users")

        userRef.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (userList as ArrayList<User>).clear()

                for(snap in dataSnapshot.children){
                    val user=snap.getValue(User::class.java)

                    for(id in idList!!){
                        if(user!!.getUid()==id){
                            (userList as ArrayList<User>).add(user)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}