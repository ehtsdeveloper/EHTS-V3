package com.ehts.ehtswatch

//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class MainActivity : Activity() {

        private var auth: FirebaseAuth? = null
        private var logoutbtn: Button? = null

        private var hrbtn: Button? = null
        private var textView: TextView? = null
        private var user: FirebaseUser? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Initialize Firebase Authentication
            auth = FirebaseAuth.getInstance()
            hrbtn = findViewById(R.id.gotoHr)
            logoutbtn = findViewById(R.id.logoutbtn)
            textView = findViewById(R.id.userdetails)
            user = auth?.currentUser

            if (user == null) {
                val intent = Intent(applicationContext, loginwatch::class.java)
                startActivity(intent)
                finish()
            } else {
                textView?.text = user?.email
            }
            hrbtn?.setOnClickListener {

                val intent = Intent(applicationContext, EmployeesHome::class.java)
                startActivity(intent)
                finish()
            }

            logoutbtn?.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(applicationContext, loginwatch::class.java)
                startActivity(intent)
                finish()
            }
        }

}