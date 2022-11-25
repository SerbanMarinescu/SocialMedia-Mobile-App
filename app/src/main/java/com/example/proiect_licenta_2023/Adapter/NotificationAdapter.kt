package com.example.proiect_licenta_2023.Adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Fragments.PostDetailsFragment
import com.example.proiect_licenta_2023.Fragments.ProfileFragment
import com.example.proiect_licenta_2023.Model.Notification
import com.example.proiect_licenta_2023.Model.Post
import com.example.proiect_licenta_2023.Model.User
import com.example.proiect_licenta_2023.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class NotificationAdapter(private val mContext:Context, private val mNotification:List<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification=mNotification[position]

        if(notification.getText().equals("started following you")){
            holder.text.text="started following you"
        }
        else if(notification.getText().equals("liked your post")){
            holder.text.text="liked your post"
        }
        else if(notification.getText().contains("commented:")){
            holder.text.text=notification.getText().replace("commented:","commented: ")
        }
        else{
            holder.text.text=notification.getText()
        }



        userInfo(holder.profileImage, holder.userName, notification.getUserId())

        if(notification.isIsPost()){
            holder.postImage.visibility=View.VISIBLE
            getPostImage(holder.postImage,notification.getPostId())
        }
        else{
            holder.postImage.visibility=View.GONE
        }


        holder.itemView.setOnClickListener{
            if(notification.isIsPost()){
                val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("postId",notification.getPostId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.fragment_container, PostDetailsFragment()).commit()
            }
            else{
                val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileId",notification.getUserId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager
                    .beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }

    }

    override fun getItemCount(): Int {
        return mNotification.size
    }


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var postImage: ImageView
        var profileImage:ImageView
        var userName:TextView
        var text:TextView

        init {
            postImage=itemView.findViewById(R.id.notification_post_image)
            profileImage=itemView.findViewById(R.id.notification_profile_image)
            userName=itemView.findViewById(R.id.username_notification)
            text=itemView.findViewById(R.id.comment_notification)
        }
    }

    private fun userInfo(imageView: ImageView, userName:TextView, publiserId:String){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(publiserId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)

                    userName.text = user.getUsername()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun getPostImage(imageView: ImageView, postID:String){
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts").child(postID)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val post=snapshot.getValue(Post::class.java)
                    Picasso.get().load(post!!.getPostImage()).placeholder(R.drawable.profile).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}