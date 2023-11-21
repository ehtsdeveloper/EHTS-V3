package com.ehts.ehtswatch

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase

//ACCESS EMPLOYEES FROM FIREBASE REALTIME DATABASE
class EmployeesHome : Activity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var databaseReference: DatabaseReference
        private lateinit var eventListener: ValueEventListener
        private lateinit var dataList: MutableList<ProfileData>
        private lateinit var adapter: MyAdapterWatch
        private lateinit var dialog: AlertDialog
        private var goBack: Button? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_employees_home)

            recyclerView = findViewById(R.id.recyclerView)
            goBack = findViewById(R.id.gobackMain)
            //if next team can display more than one employee card within the recycler view that would look better
            val gridLayoutManager = GridLayoutManager(this@EmployeesHome, 1)
            recyclerView.layoutManager = gridLayoutManager

            dataList = ArrayList()
            adapter = MyAdapterWatch(this@EmployeesHome, dataList)
            recyclerView.adapter = adapter

            goBack?.setOnClickListener {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
                .child("employees")

            dialog = AlertDialog.Builder(this@EmployeesHome)
                .setMessage("Loading...")
                .setCancelable(false)
                .create()



            eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    for (itemSnapshot in snapshot.children) {
                        val data = itemSnapshot.getValue(ProfileData::class.java)
                        data?.key = itemSnapshot.key
                        data?.let { dataList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                }
            }

            dialog.show()
            databaseReference.addValueEventListener(eventListener)

        }


        override fun onDestroy() {
         super.onDestroy()
         databaseReference.removeEventListener(eventListener)
       }
}
