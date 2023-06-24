package com.EHTS.ehts_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//displaying employees test results and sending over the bluetooth data to this screen to the specific employee that was tested
public class EmployeeRecord extends AppCompatActivity {

    ImageButton backButton;
    TextView empNameRec, empIddata, agedata, heightdata, weightdata, deviceIDdata;
    ImageView imageRec;

   // TextView  editProfile;
    Button deleteProfile;

    String key = "";
    String imageUrl = "";
    CardView profileCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_record);

        imageRec = findViewById(R.id.ImageData);
        empNameRec = findViewById(R.id.EmpNameData);

        empIddata = findViewById(R.id.EmpIDData);
        agedata = findViewById(R.id.AgeData);

        heightdata = findViewById(R.id.heightData);
        weightdata = findViewById(R.id.weightData);

        deviceIDdata = findViewById(R.id.deviceIdData);
        profileCard = findViewById(R.id.profileCard);

        deleteProfile = findViewById(R.id.deleteProfile);
       // editProfile = findViewById(R.id.EditProfile);

        backButton = findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            empNameRec.setText(bundle.getString("Employee Name"));
            empIddata.setText(bundle.getString("Employee ID"));
            agedata.setText(bundle.getString("Age"));
            heightdata.setText(bundle.getString("Height (in)"));
            weightdata.setText(bundle.getString("Weight (lb)"));
            deviceIDdata.setText(bundle.getString("Device ID"));


             key = bundle.getString("Key");
            imageUrl = bundle.getString("images/");
            Glide.with(this).load(bundle.getString("images/")).into(imageRec);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(getApplicationContext(), Home_Employee.class));
                onBackPressed(); // Go back to the previous page
            }
        });


        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference;

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
               reference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("employees");


                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(EmployeeRecord.this, "Deleted", Toast.LENGTH_SHORT).show();

                        onBackPressed(); // Go back to the previous activity
                    }
                });
            }
        });



/*
didn't get this feature to work - ignore for now

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeRecord.this, Update_Profile.class)
                        .putExtra("images/", imageUrl)
                        .putExtra("Employee Name", empNameRec.getText().toString())
                        .putExtra("Employee ID", empIddata.getText().toString())
                        .putExtra("Age", agedata.getText().toString())
                        .putExtra("Height (in)", heightdata.getText().toString())
                        .putExtra("Weight (lb)", weightdata.getText().toString())
                        .putExtra("Device ID", deviceIDdata.getText().toString())
                        .putExtra("Key", key);


                startActivity(intent);
            }
        });


 */

    }
}

