package com.example.proiect_licenta_2023.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proiect_licenta_2023.R
import com.example.proiect_licenta_2023.Model.User
import com.example.proiect_licenta_2023.Adapter.UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SearchFragment : Fragment() {

    private var recyclerView:RecyclerView?=null
    private var userAdapter:UserAdapter?=null
    private var mUser:MutableList<User>?=null
    private lateinit var searchText:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView=view.findViewById(R.id.recycle_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)
        mUser=ArrayList()
        userAdapter=context?.let { UserAdapter(it, mUser as ArrayList<User>,true) }
        recyclerView?.adapter=userAdapter

        searchText=view.findViewById(R.id.search_edit_text)

        searchText.addTextChangedListener(object:TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchText.text.toString()==""){

                }
                else{
                    recyclerView?.visibility=View.VISIBLE
                    retrieveUser()
                    searchUser(s.toString().lowercase())
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        return view
    }

    private fun searchUser(input: String) {
        val query=FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("fullname").startAt(input).endAt(input+"\uf8ff")

        query.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()

                    for(snapshot in dataSnapshot.children){
                        val user=snapshot.getValue(User::class.java)
                        if(user!=null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun retrieveUser() {
        val userRef=FirebaseDatabase.getInstance().getReference().child("Users")

        userRef.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(searchText.text.toString()==""){
                    mUser?.clear()

                    for(snapshot in dataSnapshot.children){
                        val user=snapshot.getValue(User::class.java)
                        if(user!=null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}