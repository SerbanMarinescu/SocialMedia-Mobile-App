package com.example.proiect_licenta_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var addComment:EditText
    private lateinit var postComment:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent=intent

        postId=intent.getStringExtra("postId")!!
        publisherId=intent.getStringExtra("publisherId")!!

        firebaseUser=FirebaseAuth.getInstance().currentUser
        addComment=findViewById(R.id.add_comment)
        postComment=findViewById(R.id.post_comment)

        postComment.setOnClickListener(View.OnClickListener {
            if(addComment.text.toString()==null){
                Toast.makeText(this@CommentsActivity,"Please write comment first",Toast.LENGTH_LONG).show()
            }
            else{
                addComment()
            }
        })

        retrieveUserImage()

    }

    private fun retrieveUserImage(){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)
                    val profile_img=findViewById<CircleImageView>(R.id.pro_image_profile_frag)
                    val profile_img_settings=findViewById<CircleImageView>(R.id.profile_image_comment)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_img_settings)

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

        addComment.text.clear()

    }



}