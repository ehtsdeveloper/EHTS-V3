package com.ehts.ehtswatch
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

//didn't work instead it was trying to overwrite data - you can delete any activity you don't end up using
//kept these just in case next team decides they want to add them or work on imporving the wearOS UI
class EMPStartTest : Activity() {
    private var auth: FirebaseAuth? = null

    private lateinit var startTest: Button
    private lateinit var goBackButton: Button
    private var logoutbtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emp_start_test)

        // Find the buttons in the layout
        auth = FirebaseAuth.getInstance()
        startTest = findViewById(R.id.startTest)
        goBackButton = findViewById(R.id.gobackbtn)
        logoutbtn = findViewById(R.id.logoutbtn)

        // Set click listener for the "Start Test" button
        startTest.setOnClickListener {
            // Start the HeartRate activity
            val intent = Intent(this, HeartRate::class.java)
            startActivity(intent)

            // Display a toast message
            Toast.makeText(this, "Test started", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for the "Go Back" button
        goBackButton.setOnClickListener {
            // Finish the current activity and go back to the previous activity
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
