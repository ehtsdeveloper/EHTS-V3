package com.ehts.ehtswatch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView

//displays which user we are testing and allows you to start the test specifically for that user
class EmployeeRecord : Activity() {

    private lateinit var backButton: Button
    private lateinit var empNameRec: TextView
    private lateinit var empIddata: TextView
   // private lateinit var agedata: TextView
   // private lateinit var heightdata: TextView
   // private lateinit var weightdata: TextView
    private lateinit var genderdata: TextView
   // private lateinit var imageRec: ImageView
    private lateinit var startTestbtn: Button
   // private lateinit var profileCard: CardView

    private var key: String = ""
    //private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_record)

       // imageRec = findViewById(R.id.ImageData)
        empNameRec = findViewById(R.id.EmpNameData)
        empIddata = findViewById(R.id.EmpIDData)
     //   agedata = findViewById(R.id.AgeData)
    //    heightdata = findViewById(R.id.heightData)
    //    weightdata = findViewById(R.id.weightData)
        genderdata = findViewById(R.id.genderData)
   //  profileCard = findViewById(R.id.recCard)
      startTestbtn = findViewById(R.id.startTest)
        backButton = findViewById(R.id.gobackbtn)

        val bundle = intent.extras
        if (bundle != null) {
            empNameRec.text = bundle.getString("Employee Name")
            empIddata.text = bundle.getString("Employee ID")
         //   agedata.text = bundle.getString("Age")
          //  heightdata.text = bundle.getString("Height (in)")
        //    weightdata.text = bundle.getString("Weight (lb)")
            genderdata.text = bundle.getString("Gender")

            key = bundle.getString("Key")!!
         //   imageUrl = bundle.getString("images/")!!
       //     Glide.with(this).load(bundle.getString("images/")).into(imageRec)
        }
        startTestbtn.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        backButton.setOnClickListener {
            // Navigate to HeartRate Activity
            val intent = Intent(applicationContext, HeartRate::class.java)
            startActivity(intent)
            finish()
        }


    }
}