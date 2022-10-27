package com.example.proiect_licenta_2023

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.proiect_licenta_2023.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var logoutBtn:Button
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var saveChangesBtn:ImageView
    private lateinit var changeImageBTN:TextView
    private lateinit var imageProfile:CircleImageView
    private lateinit var fullName:TextView
    private lateinit var username:TextView
    private lateinit var BIO:TextView
    private var checker=""
    private var myUrl=""
    private var imageUri:Uri?=null
    private var storageProfilePicRef: StorageReference?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        fullName=findViewById(R.id.full_name_profile_frag)
        username=findViewById(R.id.Username_profile_frag)
        BIO=findViewById(R.id.bio_profile_frag)

        logoutBtn=findViewById(R.id.logout_btn)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!

        storageProfilePicRef=FirebaseStorage.getInstance().reference.child("Profile Pictures")

        imageProfile=findViewById(R.id.profile_image_view_profile_frag)

        logoutBtn.setOnClickListener{

            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this@AccountSettingsActivity,SingInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        saveChangesBtn=findViewById(R.id.save_info_profile_btn)
        changeImageBTN=findViewById(R.id.change_image_text_btn)

        changeImageBTN.setOnClickListener {
            checker="clicked"
            CropImage.activity().setAspectRatio(1,1).start(this@AccountSettingsActivity)
        }

        saveChangesBtn.setOnClickListener{
            if(checker=="clicked"){
                uploadImageAndUpdateInfo()
            }
            else{
                updateUserInfoOnly()
            }
        }

        userInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            imageProfile.setImageURI(imageUri)
        }
        else{
            Toast.makeText(this,"Error changing icon", Toast.LENGTH_LONG).show()
        }

    }

    private fun updateUserInfoOnly() {
        val userRef= FirebaseDatabase.getInstance().reference.child("Users")

        if(fullName.text.toString()==""){
            Toast.makeText(this,"Please write fullname first!", Toast.LENGTH_LONG).show()
        }

        if(username.text.toString()==""){
            Toast.makeText(this,"Please write username first!", Toast.LENGTH_LONG).show()
        }

        val userMap=HashMap<String,Any>()

        userMap["fullname"]=(fullName.text.toString()).lowercase()
        userMap["username"]=(username.text.toString()).lowercase()
        userMap["bio"]=(BIO.text.toString()).lowercase()

        userRef.child(firebaseUser.uid).updateChildren(userMap)

        Toast.makeText(this,"Account Information has been saved successfuly",Toast.LENGTH_LONG).show()

        val intent=Intent(this@AccountSettingsActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()

    }

    private fun userInfo(){
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    val user=snapshot.getValue<User>(User::class.java)
                    val profile_img=findViewById<CircleImageView>(R.id.pro_image_profile_frag)
                    val profile_img_settings=findViewById<CircleImageView>(R.id.profile_image_view_profile_frag)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_img_settings)


                    val profile_username=findViewById<TextView>(R.id.Username_profile_frag)
                    val profile_fullname=findViewById<TextView>(R.id.full_name_profile_frag)
                    val profile_bio=findViewById<TextView>(R.id.bio_profile_frag)

                    profile_username?.setText(user.getUsername())
                    profile_fullname?.setText(user.getFullname())
                    profile_bio?.setText(user.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun uploadImageAndUpdateInfo() {

        when{
            imageUri==null -> Toast.makeText(this,"Select image first!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(fullName.text.toString()) -> Toast.makeText(this,"Please write fullname first!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username.text.toString()) -> Toast.makeText(this,"Please write username first!", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog=ProgressDialog(this)
                progressDialog.setTitle("Account settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileReference=storageProfilePicRef!!.child(firebaseUser.uid+".jpg")
                var uploadTask:StorageTask<*>
                uploadTask=fileReference.putFile(imageUri!!)
                uploadTask.continueWithTask<Uri>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if(!task.isSuccessful){
                        val eroare=task.exception.toString()
                        Toast.makeText(this,"$eroare",Toast.LENGTH_LONG).show()
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileReference.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> {task ->
                    if(task.isSuccessful){
                        val downloadUrl=task.result
                        myUrl=downloadUrl.toString()

                        val ref=FirebaseDatabase.getInstance().reference.child("Users")
                        val userMap=HashMap<String,Any>()

                        userMap["fullname"]=(fullName.text.toString()).lowercase()
                        userMap["username"]=(username.text.toString()).lowercase()
                        userMap["bio"]=(BIO.text.toString()).lowercase()
                        userMap["image"]=myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this,"Account Information has been saved successfuly",Toast.LENGTH_LONG).show()

                        val intent=Intent(this@AccountSettingsActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }
                })
            }
        }

    }
}