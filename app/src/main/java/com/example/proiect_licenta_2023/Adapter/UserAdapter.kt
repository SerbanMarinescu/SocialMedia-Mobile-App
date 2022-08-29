package com.example.proiect_licenta_2023.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Model.User
import com.example.proiect_licenta_2023.R
import com.squareup.picasso.Picasso

class UserAdapter (private var mContext:Context,
                   private var mUser:List<User>,
                   private var isFragment:Boolean=false):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user=mUser[position]
        holder.userNameTextView.text=user.getUsername()
        holder.fullNameTextView.text=user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){

        var userNameTextView:TextView=itemView.findViewById(R.id.user_name_search)
        var fullNameTextView:TextView=itemView.findViewById(R.id.user_fullname_search)
        var followBtn:Button=itemView.findViewById(R.id.follow_btn_search)
        var userProfileImage:ImageView=itemView.findViewById(R.id.user_profile_image_search)


    }
}