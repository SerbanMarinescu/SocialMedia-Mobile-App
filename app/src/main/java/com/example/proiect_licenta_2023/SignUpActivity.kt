package com.example.proiect_licenta_2023

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {

    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var username:EditText
    private lateinit var mAuth:FirebaseAuth
    private lateinit var ProgressDialog:ProgressDialog
    private lateinit var sgn_in_btn:Button
    private lateinit var sgn_up_btn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        name=findViewById(R.id.fullname_signup)
        email=findViewById(R.id.email_signup)
        username=findViewById(R.id.username_signup)
        password=findViewById(R.id.password_signup)
        mAuth=FirebaseAuth.getInstance()
        ProgressDialog=ProgressDialog(this@SignUpActivity)
        sgn_in_btn=findViewById(R.id.signin_link_btn)
        sgn_up_btn=findViewById(R.id.signup_btn)

        sgn_in_btn.setOnClickListener {
            startActivity(Intent(this, SingInActivity::class.java))
        }

        sgn_up_btn.setOnClickListener {
            CreateAccount()
        }

    }

    private fun CreateAccount() {

        val FullName=name.text.toString()
        val Email=email.text.toString()
        val Username= username.text.toString()
        val Password= password.text.toString()

        when{
            TextUtils.isEmpty(FullName) -> Toast.makeText(this,"Full Name is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(Email) -> Toast.makeText(this,"Email is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(Username) -> Toast.makeText(this,"Username is required",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(Password) -> Toast.makeText(this,"Password is required",Toast.LENGTH_LONG).show()

            else ->{

                ProgressDialog.setTitle("SignUp")
                ProgressDialog.setMessage("Please wait, this may take a while...")
                ProgressDialog.setCanceledOnTouchOutside(false)
                ProgressDialog.show()

                mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        saveUserInfo(FullName,Username,Email,Password,ProgressDialog)
                    }
                    else{
                        val message=task.exception.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        ProgressDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun saveUserInfo(fullName: String, username: String, email: String, password: String, progressDialog:ProgressDialog) {
        val currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef:DatabaseReference=FirebaseDatabase.getInstance().reference.child("Users")
        val userMap=HashMap<String,Any>()

        userMap["uid"]=currentUserId
        userMap["fullname"]=fullName.lowercase()
        userMap["username"]=username.lowercase()
        userMap["email"]=email
        userMap["bio"]="Hey I am using Serban&Lidia's app"
        userMap["image"]="https://firebasestorage.googleapis.com/v0/b/licenta2023.appspot.com/o/Default%20Images%2Fnoprofile.png?alt=media&token=296be940-0837-4b60-8edb-ac551b20ea49"

        usersRef.child(currentUserId).setValue(userMap).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this,"Account has been created successfuly",Toast.LENGTH_LONG).show()


                FirebaseDatabase.getInstance().reference.child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId).setValue(true)


                val intent=Intent(this@SignUpActivity,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else{
                val message=task.exception.toString()
                Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                mAuth.signOut()
                progressDialog.dismiss()
            }
        }
    }
}