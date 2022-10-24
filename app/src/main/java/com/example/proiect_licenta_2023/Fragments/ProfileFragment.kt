package com.example.proiect_licenta_2023.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.proiect_licenta_2023.AccountSettingsActivity
import com.example.proiect_licenta_2023.Model.User
import com.example.proiect_licenta_2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var profileId:String
    private lateinit var firebaseUser:FirebaseUser
    private lateinit var edit_account_btn:Button
    private lateinit var total_followers:TextView
    private lateinit var total_followings:TextView
    private lateinit var profile_img:CircleImageView
    private lateinit var profile_username:TextView
    private lateinit var profile_fullname:TextView
    private lateinit var profile_bio:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!
        edit_account_btn=view?.findViewById(R.id.edit_account_settings_btn)!!
        total_followers=view.findViewById(R.id.total_followers)
        total_followings= view.findViewById(R.id.total_following)
        profile_img=view.findViewById(R.id.pro_image_profile_frag)
        profile_username= view.findViewById(R.id.profile_fragment_username)
        profile_fullname=view.findViewById(R.id.full_name_profile_frag)
        profile_bio=view.findViewById(R.id.bio_profile_frag)

        val pref=context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)


        if(pref!=null){
            this.profileId= pref.getString("profileId","none").toString()
        }


        if(profileId == firebaseUser.uid){

            edit_account_btn.text = "Edit Profile"
        }
        else if(profileId != firebaseUser.uid){
            checkFollowandFollowing()
        }


        edit_account_btn.setOnClickListener {
           val getBtnText=edit_account_btn.text.toString()

            when  {
                getBtnText=="Edit Profile" -> startActivity(Intent(context,AccountSettingsActivity::class.java))

                getBtnText=="Follow" -> {
                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileId)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                }
                getBtnText== "Following" -> {

                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileId)
                            .child("Followers").child(it.toString()).removeValue()
                    }
                }
            }
        }

        getFollowers()
        getFollowings()
        userInfo()

        return view
    }

    private fun checkFollowandFollowing() {

        val followingRef= firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it.toString())
                .child("Following")
        }


        if(followingRef.toString()!=""){

            followingRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child(profileId).exists()){
                        edit_account_btn.text="Following"
                    }
                    else{
                        edit_account_btn.text="Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    private fun getFollowers(){
        val followersRef= FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileId)
            .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    total_followers.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    private fun getFollowings(){
        val followersRef= FirebaseDatabase.getInstance().reference.child("Follow")
            .child(profileId)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    total_followings.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun userInfo(){
        val userRef=FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_img)

                    profile_username.text = user.getUsername()
                    profile_fullname.text = user.getFullname()
                    profile_bio.text = user.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref=context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }




}