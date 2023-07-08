package com.EHTS.ehts_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//displaying employees test results and sending over the bluetooth data to this screen to the specific employee that was tested
public class EmployeeRecord extends AppCompatActivity {

    ImageButton backButton;
    TextView empNameRec, empIddata, agedata, heightdata, weightdata, deviceIDdata;
    ImageView imageRec;

    TextView tvLow, tvResting, tvMax;
    TextView AvgtvLow, AvgtvResting, AvgtvMax;

    TextView tvFinalResult;
    CardView cardFinalResult;

   // TextView  editProfile;
    Button deleteProfile;

    String key = "";
    String empId = "";
    String imageUrl = "";
    CardView profileCard;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;

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

        tvLow = findViewById(R.id.tv_low);
        tvResting = findViewById(R.id.tv_resting);
        tvMax = findViewById(R.id.tv_max);


        AvgtvLow = findViewById(R.id.Avgtv_low);
        AvgtvResting = findViewById(R.id.Avgtv_resting);
        AvgtvMax = findViewById(R.id.Avgtv_max);


        tvFinalResult = findViewById(R.id.tvFinalResult);
        cardFinalResult = findViewById(R.id.cardFinalResult);

       // editProfile = findViewById(R.id.EditProfile);

        backButton = findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            empNameRec.setText(bundle.getString("Employee Name"));
            empIddata.setText(bundle.getString("Employee ID"));
            empId = bundle.getString("Employee ID");
            agedata.setText(bundle.getString("Age"));
            heightdata.setText(bundle.getString("Height (in)"));
            weightdata.setText(bundle.getString("Weight (lb)"));
            deviceIDdata.setText(bundle.getString("Device ID"));


             key = bundle.getString("Key");
            imageUrl = bundle.getString("images/");
            Glide.with(this).load(bundle.getString("images/")).into(imageRec);
        }

        fetchSensorsData();
        fetchSensorsData2();
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
    private void fetchSensorsData() {
        //fetch latest test results data of Low, max, and resting hr
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("employees").child(empId).child("sensors_record").child(empId);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Retrieve the last child node
                DataSnapshot lastChildSnapshot = null;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    lastChildSnapshot = childSnapshot;
                }

                if (lastChildSnapshot != null) {
                    String childKey = lastChildSnapshot.getKey();
                    SensorsData data = lastChildSnapshot.getValue(SensorsData.class);
                    data.setKey(childKey);

                    // Show Data
                    if (data.getLow() != null) {
                        tvLow.setText(String.valueOf(data.getLow().intValue()));
                    }

                    if (data.getResting() != null) {
                        tvResting.setText(String.valueOf(data.getResting().intValue()));
                    }

                    if (data.getMax() != null) {
                        tvMax.setText(String.valueOf(data.getMax().intValue()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchSensorsData2() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("employees").child(empId).child("sensors_record").child(empId);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long childrenCount = snapshot.getChildrenCount();
                int lowSum = 0;
                int restingSum = 0;
                int maxSum = 0;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Access the data of each child node
                    SensorsData data = childSnapshot.getValue(SensorsData.class);

                    // Add heart rate values to the sums
                    if (data != null) {
                        if (data.getLow() != null) {
                            lowSum += data.getLow().intValue();
                        }

                        if (data.getResting() != null) {
                            restingSum += data.getResting().intValue();
                        }

                        if (data.getMax() != null) {
                            maxSum += data.getMax().intValue();
                        }
                    }
                }

                // Calculate and display the averages
                if (childrenCount > 0) {
                    int avgLow = lowSum / (int) childrenCount;
                    int avgResting = restingSum / (int) childrenCount;
                    int avgMax = maxSum / (int) childrenCount;

                    AvgtvLow.setText(String.valueOf(avgLow));
                    AvgtvResting.setText(String.valueOf(avgResting));
                    AvgtvMax.setText(String.valueOf(avgMax));

                    // Determine if the employee passed or failed the EHTS Exam
                    int age = Integer.parseInt(agedata.getText().toString());
                    int restingHR = avgResting;
                    int maxHR = avgMax;

                    // Calculate the target heart rate ranges based on age
                    int maxAgeRelatedHR = 220 - age;
                    int moderateIntensityLowerLimit = (int) (maxAgeRelatedHR * 0.64);
                    int moderateIntensityUpperLimit = (int) (maxAgeRelatedHR * 0.76);
                    int vigorousIntensityLowerLimit = (int) (maxAgeRelatedHR * 0.77);
                    int vigorousIntensityUpperLimit = (int) (maxAgeRelatedHR * 0.93);

                    // Check if the resting and max heart rates are within the target ranges
                    boolean isRestingHRWithinRange = restingHR >= moderateIntensityLowerLimit && restingHR <= moderateIntensityUpperLimit;
                    boolean isMaxHRWithinRange = maxHR >= vigorousIntensityLowerLimit && maxHR <= vigorousIntensityUpperLimit;

                    // Determine the final result and update the UI accordingly
                    if (isRestingHRWithinRange && isMaxHRWithinRange) {
                        tvFinalResult.setText("Employee passed EHTS Exam");
                        cardFinalResult.setCardBackgroundColor(Color.GREEN);
                    } else {
                        tvFinalResult.setText("Employee failed EHTS Exam");
                        cardFinalResult.setCardBackgroundColor(Color.parseColor("#AD2424"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
    private void fetchSensorsData2() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("employees").child(empId).child("sensors_record").child(empId);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long childrenCount = snapshot.getChildrenCount();
                int lowSum = 0;
                int restingSum = 0;
                int maxSum = 0;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Access the data of each child node
                    SensorsData data = childSnapshot.getValue(SensorsData.class);

                    // Add heart rate values to the sums
                    if (data != null) {
                        if (data.getLow() != null) {
                            lowSum += data.getLow().intValue();
                        }

                        if (data.getResting() != null) {
                            restingSum += data.getResting().intValue();
                        }

                        if (data.getMax() != null) {
                            maxSum += data.getMax().intValue();
                        }
                    }
                }

                // Calculate and display the averages
                if (childrenCount > 0) {
                    int avgLow = lowSum / (int) childrenCount;
                    int avgResting = restingSum / (int) childrenCount;
                    int avgMax = maxSum / (int) childrenCount;

                    AvgtvLow.setText(String.valueOf(avgLow));
                    AvgtvResting.setText(String.valueOf(avgResting));
                    AvgtvMax.setText(String.valueOf(avgMax));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

 */




}

