package com.example.proiect_licenta_2023

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddStoryActivity : AppCompatActivity() {

    private var myUrl=""
    private var imageUri: Uri?=null
    private var storageStoryPicRef: StorageReference?=null
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        storageStoryPicRef= FirebaseStorage.getInstance().reference.child("Story Pictures")

        firebaseUser=FirebaseAuth.getInstance().currentUser!!

        CropImage.activity().setAspectRatio(9,16).start(this@AddStoryActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            uploadStory()
        }
        else{
            Toast.makeText(this,"Error choosing image", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadStory() {
        when{
            imageUri==null -> Toast.makeText(this,"Select image first!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog= ProgressDialog(this)
                progressDialog.setTitle("Adding story")
                progressDialog.setMessage("Please wait, we are adding your story...")
                progressDialog.show()

                val fileReference=storageStoryPicRef!!.child(System.currentTimeMillis().toString()+".jpg")

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
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if(task.isSuccessful){
                        val downloadUrl=task.result
                        myUrl=downloadUrl.toString()

                        val ref= FirebaseDatabase.getInstance().reference.child("Story")
                        val storyId=ref.push().key

                        val timeEnd=System.currentTimeMillis() + 86400000 // one day

                        val storyMap=HashMap<String,Any>()

                        storyMap["userid"]=firebaseUser.uid
                        storyMap["timestart"]=ServerValue.TIMESTAMP
                        storyMap["timeend"]=timeEnd
                        storyMap["imageurl"]=myUrl
                        storyMap["storyid"]=storyId.toString()

                        ref.child(storyId.toString()).updateChildren(storyMap)

                        Toast.makeText(this,"Post uploaded successfully",Toast.LENGTH_LONG).show()

                        val intent=Intent(this@AddStoryActivity,MainActivity::class.java)
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