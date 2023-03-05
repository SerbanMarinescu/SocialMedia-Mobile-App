package com.example.proiect_licenta_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.proiect_licenta_2023.Adapter.StoryAdapter
import com.example.proiect_licenta_2023.Model.Story
import com.example.proiect_licenta_2023.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.shts.android.storiesprogressview.StoriesProgressView
import org.w3c.dom.Text

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    var currentUserId:String=""
    var userId:String=""
    var imageList:List<String>?=null
    var storyIdsList:List<String>?=null
    var storiesProgressView: StoriesProgressView?=null
    var counter=0
    var pressTime=0L
    var limit=500L
    private val onTouchListener=View.OnTouchListener { view, motionEvent ->
        when(motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                pressTime=System.currentTimeMillis()
                storiesProgressView!!.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP -> {
                val now=System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener limit<now-pressTime
            }
        }

        false }
    private lateinit var seenNumber:TextView
    private lateinit var story_profile_image:CircleImageView
    private lateinit var story_username:TextView
    private lateinit var image_story:ImageView
    private lateinit var layout_seen:LinearLayout
    private lateinit var story_delete:TextView
    private lateinit var reverse:View
    private lateinit var skip:View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        userId= intent.getStringExtra("userId").toString()

        seenNumber=findViewById(R.id.seen_number)
        story_profile_image=findViewById(R.id.story_profile_image)
        story_username=findViewById(R.id.story_username)
        storiesProgressView=findViewById(R.id.stories_progress)
        image_story=findViewById(R.id.image_story)
        layout_seen=findViewById(R.id.layout_seen)
        story_delete=findViewById(R.id.story_delete)
        reverse=findViewById(R.id.reverse)
        skip=findViewById(R.id.skip)

        layout_seen.visibility=View.GONE
        story_delete.visibility=View.GONE

        if(userId==currentUserId){
            layout_seen.visibility=View.VISIBLE
            story_delete.visibility=View.VISIBLE
        }

        reverse.setOnClickListener{
            storiesProgressView!!.reverse()
        }

        reverse.setOnTouchListener(onTouchListener)

        skip.setOnClickListener{
            storiesProgressView!!.skip()
        }

        skip.setOnTouchListener(onTouchListener)

        seenNumber.setOnClickListener{
            val intent= Intent(this@StoryActivity,ShowUsersActivity::class.java)
            intent.putExtra("id",userId)
            intent.putExtra("storyid", storyIdsList!![counter])
            intent.putExtra("title", "views")
            startActivity(intent)
        }

        story_delete.setOnClickListener{
            val ref=FirebaseDatabase.getInstance().reference.child("Story")
                .child(userId).child(storyIdsList!![counter])

            ref.removeValue().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this@StoryActivity, "Deleted",Toast.LENGTH_LONG).show()
                }
            }
        }

        getStories(userId)
        userInfo(userId)


    }

    private fun seenNumber(storyId: String){

        val ref=FirebaseDatabase.getInstance().reference.child("Story")
            .child(userId).child(storyId).child("views")

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                seenNumber.text="" + snapshot.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addViewToStory(storyId: String){
        val ref=FirebaseDatabase.getInstance().reference.child("Story")
            .child(userId).child(storyId).child("views")
            .child(currentUserId).setValue(true)
    }

    private fun userInfo( userId: String){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(story_profile_image)
                    story_username.text=user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getStories(userId: String){
        val ref=FirebaseDatabase.getInstance().reference.child("Story")
            .child(userId)

        imageList=ArrayList()
        storyIdsList=ArrayList()

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (imageList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()

                for(snap in snapshot.children){
                    val story:Story?=snap.getValue(Story::class.java)
                    val timeCurrent=System.currentTimeMillis()

                    if(timeCurrent>story!!.getTimeStart() && timeCurrent<story.getTimeEnd()){
                        (imageList as ArrayList<String>).add(story.getImageUrl())
                        (storyIdsList as ArrayList<String>).add(story.getStoryId())
                    }
                }

                storiesProgressView!!.setStoriesCount((imageList as ArrayList<String>).size)
                storiesProgressView!!.setStoryDuration(5000L)
                storiesProgressView!!.setStoriesListener(this@StoryActivity)
                storiesProgressView!!.startStories(counter)

                Picasso.get().load(imageList!!.get(counter)).placeholder(R.drawable.profile).into(image_story)

                addViewToStory(storyIdsList!!.get(counter))
                seenNumber(storyIdsList!!.get(counter))
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onNext() {
        Picasso.get().load(imageList!![++counter]).placeholder(R.drawable.profile).into(image_story)
        addViewToStory(storyIdsList!![counter])
        seenNumber(storyIdsList!![counter])
    }

    override fun onPrev() {
        if(counter-1<0) return
        Picasso.get().load(imageList!![--counter]).placeholder(R.drawable.profile).into(image_story)
        seenNumber(storyIdsList!![counter])
    }

    override fun onComplete() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()
    }
}