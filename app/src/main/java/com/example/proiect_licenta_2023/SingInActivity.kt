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


class SingInActivity : AppCompatActivity() {

    private lateinit var sgn_up_btn:Button
    private lateinit var sgnInBtn:Button
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var ProgressDialog:ProgressDialog
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        sgn_up_btn=findViewById(R.id.signup_link_btn)
        sgnInBtn=findViewById(R.id.login_btn)
        email=findViewById(R.id.email_login)
        password=findViewById(R.id.password_login)
        ProgressDialog=ProgressDialog(this@SingInActivity)
        mAuth=FirebaseAuth.getInstance()

        sgn_up_btn.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        sgnInBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val Email=email.text.toString()
        val Password=password.text.toString()

        when{
            TextUtils.isEmpty(Email) -> Toast.makeText(this,"Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(Password) -> Toast.makeText(this,"Password is required", Toast.LENGTH_LONG).show()

            else ->{
                ProgressDialog.setTitle("Login")
                ProgressDialog.setMessage("Please wait, this may take a while...")
                ProgressDialog.setCanceledOnTouchOutside(false)
                ProgressDialog.show()

                mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener{
                    task->

                    if(task.isSuccessful){
                        ProgressDialog.dismiss()
                        val intent=Intent(this@SingInActivity,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
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

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser!=null){
            val intent=Intent(this@SingInActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}