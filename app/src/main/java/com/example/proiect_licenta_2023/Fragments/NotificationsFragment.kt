package com.example.proiect_licenta_2023.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.Adapter.NotificationAdapter
import com.example.proiect_licenta_2023.Model.Notification
import com.example.proiect_licenta_2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

    private var recyclerView: RecyclerView?=null
    private var notificationList:List<Notification>?=null
    private var notificationAdapter:NotificationAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_notifications, container, false)

        recyclerView=view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager= LinearLayoutManager(context)

        notificationList=ArrayList()
        notificationAdapter= NotificationAdapter(requireContext(),notificationList as ArrayList<Notification>)
        recyclerView?.adapter=notificationAdapter

        readNotifications()

        return view
    }

    private fun readNotifications() {
        val notificationRef= FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notificationRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (notificationList as ArrayList<Notification>).clear()

                    for(snap in snapshot.children){
                        val notification=snap.getValue(Notification::class.java)
                        (notificationList as ArrayList<Notification>).add(notification!!)
                    }

                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}