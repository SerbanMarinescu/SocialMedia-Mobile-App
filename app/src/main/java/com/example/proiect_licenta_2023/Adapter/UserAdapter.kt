package com.example.proiect_licenta_2023.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Fragments.ProfileFragment
import com.example.proiect_licenta_2023.MainActivity
import com.example.proiect_licenta_2023.Model.User
import com.example.proiect_licenta_2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter (private var mContext:Context,
                   private var mUser:List<User>,
                   private var isFragment:Boolean=false):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()
        holder.fullNameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile)
            .into(holder.userProfileImage)

        checkFollowingStatus(user.getUid(),holder.followBtn,holder.unfollowBtn)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if(isFragment){
                val pref=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                pref.putString("profileId",user.getUid())
                pref.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,ProfileFragment()).commit()
            }
            else{
                val intent=Intent(mContext,MainActivity::class.java)
                intent.putExtra("publisherId",user.getUid())
                mContext.startActivity(intent)
            }
        })

        holder.unfollowBtn.setOnClickListener {
            firebaseUser?.uid.let { it ->
                FirebaseDatabase.getInstance().reference.child("Follow")
                    .child(it.toString())
                    .child("Following")
                    .child(user.getUid()).removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUser?.uid.let { it ->
                                FirebaseDatabase.getInstance().reference.child("Follow")
                                    .child(user.getUid())
                                    .child("Followers")
                                    .child(it.toString()).removeValue()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                        }
                                    }
                            }
                        }
                    }

            }
        }

        holder.followBtn.setOnClickListener {
            if (holder.followBtn.text.toString() == "Follow") {
                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString())
                        .child("Following")
                        .child(user.getUid()).setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.getUid())
                                        .child("Followers")
                                        .child(it.toString()).setValue(true)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                            }
                                        }
                                }
                            }
                        }
                }
                addNotification(user.getUid())
            }
        }
    }



    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        var userNameTextView:TextView=itemView.findViewById(R.id.user_name_search)
        var fullNameTextView:TextView=itemView.findViewById(R.id.user_fullname_search)
        var followBtn:Button=itemView.findViewById(R.id.follow_btn_search)
        var userProfileImage:ImageView=itemView.findViewById(R.id.user_profile_image_search)
        var unfollowBtn:Button=itemView.findViewById(R.id.unfollow_btn_search)


    }

    private fun checkFollowingStatus(uid: String, followBtn: Button, unfollowBtn:Button) {
       val followingRef= firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.child(uid).exists()){
                    followBtn.text="Following"
                    unfollowBtn.visibility=View.VISIBLE
                }
                else{
                    followBtn.text="Follow"
                    unfollowBtn.visibility=View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addNotification(userId:String){
        val notificationRef=FirebaseDatabase.getInstance().reference.child("Notifications").child(userId)

        val notificationMap=HashMap<String, Any>()
        notificationMap["userid"]=firebaseUser!!.uid
        notificationMap["text"]="started following you"
        notificationMap["postid"]=""
        notificationMap["ispost"]=false

        notificationRef.push().setValue(notificationMap)
    }
}