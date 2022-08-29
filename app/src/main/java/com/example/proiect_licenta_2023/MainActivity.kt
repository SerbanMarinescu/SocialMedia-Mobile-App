package com.example.proiect_licenta_2023

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.proiect_licenta_2023.Fragments.HomeFragment
import com.example.proiect_licenta_2023.Fragments.NotificationsFragment
import com.example.proiect_licenta_2023.Fragments.ProfileFragment
import com.example.proiect_licenta_2023.Fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())
        supportActionBar?.hide()

    }

    private fun moveToFragment(fragment:Fragment){

        val fragmentTrans=supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container,fragment)
        fragmentTrans.commit()
    }
}