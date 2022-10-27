package com.example.proiect_licenta_2023

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private lateinit var save_Post_Btn:ImageView
    private lateinit var imagePost:ImageView
    private lateinit var descriptionPost:EditText
    private lateinit var firebaseUser:FirebaseUser
    private var myUrl=""
    private var imageUri: Uri?=null
    private var storagePostPicRef: StorageReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef= FirebaseStorage.getInstance().reference.child("Post Pictures")
        save_Post_Btn=findViewById(R.id.save_new_post_btn)
        imagePost=findViewById(R.id.image_post)
        descriptionPost=findViewById(R.id.description_post)
        firebaseUser=FirebaseAuth.getInstance().currentUser!!


        save_Post_Btn.setOnClickListener{
            uploadImage()
        }

        CropImage.activity().setAspectRatio(2,1).start(this@AddPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            imagePost.setImageURI(imageUri)
        }
        else{
            Toast.makeText(this,"Error choosing image", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImage() {
        when{
            imageUri==null -> Toast.makeText(this,"Select image first!", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(descriptionPost.text.toString()) -> Toast.makeText(this,"Please write description first!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog= ProgressDialog(this)
                progressDialog.setTitle("Account settings")
                progressDialog.setMessage("Please wait, we are adding your post...")
                progressDialog.show()

                val fileReference=storagePostPicRef!!.child(System.currentTimeMillis().toString()+".jpg")

                var uploadTask: StorageTask<*>
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

                        val ref= FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId=ref.push().key
                        val postMap=HashMap<String,Any>()

                        postMap["postid"]=postId!!
                        postMap["description"]=descriptionPost.text.toString().lowercase()
                        postMap["publisher"]=firebaseUser.uid
                        postMap["postimage"]=myUrl

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this,"Post uploaded successfully",Toast.LENGTH_LONG).show()

                        val intent=Intent(this@AddPostActivity,MainActivity::class.java)
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