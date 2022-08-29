package com.example.proiect_licenta_2023

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var logoutBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        logoutBtn=findViewById(R.id.logout_btn)

        logoutBtn.setOnClickListener{

            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this@AccountSettingsActivity,SingInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}