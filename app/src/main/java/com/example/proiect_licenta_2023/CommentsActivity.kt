package com.example.proiect_licenta_2023

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Adapter.CommentsAdapter
import com.example.proiect_licenta_2023.Model.Comment
import com.example.proiect_licenta_2023.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsActivity : AppCompatActivity() {

    private var postId=""
    private var publisherId=""
    private var firebaseUser:FirebaseUser?=null
    private var commentsAdapter:CommentsAdapter?=null
    private var commentList:MutableList<Comment>?=null

    private lateinit var addComment:EditText
    private lateinit var postComment:TextView
    private lateinit var recyclerView:RecyclerView
    private lateinit var profile_img_settings:CircleImageView
    private lateinit var postImage:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent=intent

        postId=intent.getStringExtra("postId")!!
        publisherId=intent.getStringExtra("publisherId")!!

        firebaseUser=FirebaseAuth.getInstance().currentUser
        addComment=findViewById(R.id.add_comment)
        postComment=findViewById(R.id.post_comment)
        recyclerView=findViewById(R.id.recycler_view_comments)
        profile_img_settings=findViewById(R.id.profile_image_comment)
        postImage=findViewById(R.id.post_image_comments)

        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=true
        recyclerView.layoutManager=linearLayoutManager

        commentList=ArrayList()
        commentsAdapter= CommentsAdapter(this,commentList)
        recyclerView.adapter=commentsAdapter


        postComment.setOnClickListener(View.OnClickListener {
            if(addComment.text.toString()==null){
                Toast.makeText(this@CommentsActivity,"Please write comment first",Toast.LENGTH_LONG).show()
            }
            else{
                addComment()
            }
        })

        retrieveUserImage()
        readComments()
        getPostImage()

    }

    private fun retrieveUserImage(){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_img_settings)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getPostImage(){
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts").child(postId).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val image=snapshot.value.toString()
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(postImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addComment(){
        val commentsRef= FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        val commentsMap=HashMap<String,Any>()

        commentsMap["comment"]=addComment.text.toString()
        commentsMap["publisher"]=firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()
        addComment.text.clear()

    }

    private fun readComments(){
        val commentsRef=FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    commentList!!.clear()

                    for(p in snapshot.children){
                        val comment=p.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentsAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun addNotification(){
        val notificationRef=FirebaseDatabase.getInstance().reference.child("Notifications").child(publisherId)

        val notificationMap=HashMap<String, Any>()
        notificationMap["userid"]=firebaseUser!!.uid
        notificationMap["text"]="commented: "+addComment.text.toString()
        notificationMap["postid"]=postId
        notificationMap["ispost"]=true

        notificationRef.push().setValue(notificationMap)
    }

}