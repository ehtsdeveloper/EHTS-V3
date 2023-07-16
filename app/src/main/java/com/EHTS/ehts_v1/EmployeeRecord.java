package com.EHTS.ehts_v1;

import static android.provider.Telephony.Mms.Part.FILENAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


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

    LineChart lineChart;
    Button refreshGraph;

    Button exportData;
 //  BarChart barChart;
    private Button btnToday, btnMonth, btnYear;
    private TextView period;

    // private List<String> xValues = Arrays.asList("8am", "9am", "10am", "11am", "12am", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm");
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

        //barChart = findViewById(R.id.chart);
        lineChart = findViewById(R.id.chart);
        refreshGraph = findViewById(R.id.refreshGraph);

       // btnToday = findViewById(R.id.btnToday);
       // btnMonth = findViewById(R.id.btnMonth);
      //  btnYear = findViewById(R.id.btnYear);
        period = findViewById(R.id.period);
        exportData = findViewById(R.id.exportData);


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
        fetchHeartRateData();
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


        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDataToCSV(view);
            }
        });


/*
//work in progress - provided the link to the youtube video I followed for this in the design document

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

        /*
        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current date and format it
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
                String todayDate = dateFormat.format(new Date());

                // Set the period TextView to display today's date
                period.setText("Today's date: " + todayDate);

                // Call the method to fetch and display the data for today
                fetchDataForToday();
            }
        });

        btnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current month and year
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String currentMonthYear = dateFormat.format(new Date());

                // Set the period TextView to display the current month and year
                period.setText(currentMonthYear);

                // Call the method to fetch and display the data for the current month
                fetchDataForCurrentMonth();
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current year
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                String currentYear = dateFormat.format(new Date());

                // Set the period TextView to display the current year
                period.setText("Year " + currentYear);

                // Call the method to fetch and display the data for the current year
                fetchDataForCurrentYear();
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
        // retrieves the avg of all test results low, max, and resting also determines pass/fail test
        //could just make pass/fail results a manual input?
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

                    // Determine if the employee passed or failed the EHTS Exam -- this equation still needs work
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

    private void fetchHeartRateData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference heartRateRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record");

        eventListener = heartRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.child(empId).getChildren()) {
                    String recordingTimeStamp = childSnapshot.child("recordingTimeStamp").getValue(String.class);
                    Integer low = childSnapshot.child("low").getValue(Integer.class);
                    Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                    Integer max = childSnapshot.child("max").getValue(Integer.class);

                    if (low != null) {
                        entries.add(new BarEntry(entries.size(), low.intValue(), recordingTimeStamp));
                    }

                    if (resting != null) {
                        entries.add(new BarEntry(entries.size(), resting.intValue(), recordingTimeStamp));
                    }

                    if (max != null) {
                        entries.add(new BarEntry(entries.size(), max.intValue(), recordingTimeStamp));
                    }
                }

                BarDataSet dataSet = new BarDataSet(entries, "Heart Rate (BPM)");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                YAxis yAxis = barChart.getAxisLeft();
                yAxis.setAxisMaximum(0f);
                yAxis.setAxisMaximum(150f);
                yAxis.setAxisLineWidth(1f);
                yAxis.setAxisLineColor(Color.WHITE);
                yAxis.setLabelCount(10);

                barChart.getDescription().setEnabled(false);
                barChart.setBackgroundColor(Color.BLACK);
                barChart.getXAxis().setTextColor(Color.WHITE);
                barChart.getAxisLeft().setTextColor(Color.WHITE);
                barChart.getAxisRight().setEnabled(false);
                barChart.invalidate();

                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getTimestamps(entries)));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


 */


/*
    private void updateBarChart(ArrayList<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Heart Rate (BPM)");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMaximum(0f);
        yAxis.setAxisMaximum(150f);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.getXAxis().setTextColor(Color.BLACK);
        barChart.getAxisLeft().setTextColor(Color.BLACK);
        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getTimestamps(entries)));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
    }


    private void fetchDataForToday() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

        // Query the database to fetch the data for today's date
        Query query = dataRef.orderByChild("recordingStartTime");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : nodeSnapshot.getChildren()) {
                        String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                        String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                        Integer low = childSnapshot.child("low").getValue(Integer.class);
                        Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                        Integer max = childSnapshot.child("max").getValue(Integer.class);

                        // Add the heart rate values to the entries list
                        if (low != null) {
                            entries.add(new BarEntry(entries.size(), low.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (resting != null) {
                            entries.add(new BarEntry(entries.size(), resting.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (max != null) {
                            entries.add(new BarEntry(entries.size(), max.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                    }
                }

                // Update the bar chart with the fetched data
                updateBarChart(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchDataForCurrentMonth() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

        // Query the database to fetch the data for the current month
        Query query = dataRef.orderByChild("recordingStartTime");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : nodeSnapshot.getChildren()) {
                        String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                        String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                        Integer low = childSnapshot.child("low").getValue(Integer.class);
                        Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                        Integer max = childSnapshot.child("max").getValue(Integer.class);

                        // Add the heart rate values to the entries list
                        if (low != null) {
                            entries.add(new BarEntry(entries.size(), low.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (resting != null) {
                            entries.add(new BarEntry(entries.size(), resting.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (max != null) {
                            entries.add(new BarEntry(entries.size(), max.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                    }
                }

                // Update the bar chart with the fetched data
                updateBarChart(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataForCurrentYear() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

        // Query the database to fetch the data for the current year
        Query query = dataRef.orderByChild("recordingStartTime");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (DataSnapshot nodeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : nodeSnapshot.getChildren()) {
                        String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                        String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                        Integer low = childSnapshot.child("low").getValue(Integer.class);
                        Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                        Integer max = childSnapshot.child("max").getValue(Integer.class);

                        // Add the heart rate values to the entries list
                        if (low != null) {
                            entries.add(new BarEntry(entries.size(), low.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (resting != null) {
                            entries.add(new BarEntry(entries.size(), resting.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                        if (max != null) {
                            entries.add(new BarEntry(entries.size(), max.intValue(), recordingStartTimestamp + " - " + recordingStopTimestamp));
                        }
                    }
                }

                // Update the bar chart with the fetched data
                updateBarChart(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

 */
private void fetchHeartRateData() {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference heartRateRef = FirebaseDatabase.getInstance().getReference("users")
            .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

    Query query = heartRateRef.orderByChild("recordingStartTime");
    query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<Entry> lowEntries = new ArrayList<>();
            ArrayList<Entry> restingEntries = new ArrayList<>();
            ArrayList<Entry> maxEntries = new ArrayList<>();

            long minutesSinceStart;
            long minutesGraphStart = 10160;

            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                Integer low = childSnapshot.child("low").getValue(Integer.class);
                Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                Integer max = childSnapshot.child("max").getValue(Integer.class);

                try {
                    minutesSinceStart = convertToHoursSinceCustomEpoch(recordingStartTimestamp);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (minutesSinceStart >= minutesGraphStart) {
                    if (low != null) {
                        try {
                            lowEntries.add(new Entry(minutesSinceStart, low.intValue()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }

                    if (resting != null) {
                        try {
                            restingEntries.add(new Entry(minutesSinceStart, resting.intValue()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }

                    if (max != null) {
                        try {
                            maxEntries.add(new Entry(minutesSinceStart, max.intValue()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            // Calculate the average resting heart rate
            float avgRestingHeartRate = calculateAverage(restingEntries);

            // Create a new LimitLine for the average resting heart rate
            LimitLine avgRestingLine = new LimitLine(avgRestingHeartRate, "Avg Resting HR");
            avgRestingLine.setLineColor(Color.BLUE);
            avgRestingLine.setLineWidth(2f);

            // Clear the existing LimitLines from the chart
            lineChart.getAxisLeft().removeAllLimitLines();

            // Add the new average resting line to the chart
            lineChart.getAxisLeft().addLimitLine(avgRestingLine);

            // Configure the low line chart
            LineDataSet dataSetLow = new LineDataSet(lowEntries, "Low HR");
            dataSetLow.setColor(Color.YELLOW);
            dataSetLow.setLineWidth(2f);
            LineData lineDataLow = new LineData(dataSetLow);

            // Configure the resting line chart
            LineDataSet dataSetResting = new LineDataSet(restingEntries, "Resting HR");
            dataSetResting.setColor(Color.GREEN);
            dataSetResting.setLineWidth(2f);
            LineData lineDataResting = new LineData(dataSetResting);

            // Configure the Max line chart
            LineDataSet dataSetMax = new LineDataSet(maxEntries, "Max HR");
            dataSetMax.setColor(Color.RED);
            dataSetMax.setLineWidth(2f);
            LineData lineDataMax = new LineData(dataSetMax);

            // Merge the three LineData objects into a single LineData object
            LineData combinedLineData = new LineData();
            combinedLineData.addDataSet(dataSetLow);
            combinedLineData.addDataSet(dataSetResting);
            combinedLineData.addDataSet(dataSetMax);

            // Set the combined LineData as the data for the line chart
            lineChart.setData(combinedLineData);

            /*
            Configure graph axis
             */
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setAxisMaximum(0f);
            yAxis.setAxisMaximum(150f);
            yAxis.setAxisLineWidth(1f);
            yAxis.setAxisLineColor(Color.BLACK);
            yAxis.setLabelCount(10);

            lineChart.getDescription().setEnabled(false);
            lineChart.setBackgroundColor(Color.WHITE);
            lineChart.getXAxis().setTextColor(Color.BLACK);
            lineChart.getAxisLeft().setTextColor(Color.BLACK);
            lineChart.getAxisRight().setEnabled(false);

            lineChart.invalidate();

            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisLabels()));
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

            lineChart.getXAxis().setGranularity(1f);
            lineChart.getXAxis().setGranularityEnabled(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

    refreshGraph.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fetchHeartRateData();
            // Display a toast message
            Toast.makeText(EmployeeRecord.this, "Graph Refreshed", Toast.LENGTH_SHORT).show();
        }
    });
}



    public static long convertToHoursSinceCustomEpoch(String dateTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = format.parse(dateTime);

        // Define custom Epoch
        Date customEpoch = format.parse("2023-07-01 00:00:00");

        long diffInMilli = date.getTime() - customEpoch.getTime();
        long hoursSinceCustomEpoch = diffInMilli / 1000 / 60; // / 60;
        return hoursSinceCustomEpoch;
    }
    private String[] getXAxisLabels() {
        String[] labels = new String[9];
        labels[0] = "4AM";
        labels[1] = "6AM";
        labels[2] = "8AM";
        labels[3] = "10AM";
        labels[4] = "12PM";
        labels[5] = "2PM";
        labels[6] = "4PM";
        labels[7] = "6PM";
        labels[8] = "8PM";
        return labels;
    }

    private float getTimeInHours(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH", Locale.getDefault());
        try {
            Date date = inputFormat.parse(timestamp);
            return Float.parseFloat(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private float calculateAverage(ArrayList<Entry> entries) {
        float sum = 0;
        for (Entry entry : entries) {
            sum += entry.getY();
        }
        return sum / entries.size();
    }







/*


//bar
// right now this works retrieving all the data from the database and displays them on the graph to the correct employee
//but the top code has a problem displaying certain periods based on the timestamps need to fix this

    private void fetchHeartRateData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference heartRateRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

        Query query = heartRateRef.orderByChild("recordingStartTime");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> entries = new ArrayList<>();

                int lowSum = 0;
                int restingSum = 0;
                int maxSum = 0;
                int entryCount = 0;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                    String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                    Integer low = childSnapshot.child("low").getValue(Integer.class);
                    Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                    Integer max = childSnapshot.child("max").getValue(Integer.class);

                    if (low != null) {
                        entries.add(new BarEntry(entries.size(), low.intValue(), recordingStartTimestamp));
                        lowSum += low.intValue();
                        entryCount++;
                    }

                    if (resting != null) {
                        entries.add(new BarEntry(entries.size(), resting.intValue(), recordingStartTimestamp));
                        restingSum += resting.intValue();
                        entryCount++;
                    }

                    if (max != null) {
                        entries.add(new BarEntry(entries.size(), max.intValue(), recordingStartTimestamp));
                        maxSum += max.intValue();
                        entryCount++;
                    }
                }

                // Calculate the average resting heart rate
                float avgRestingHeartRate = restingSum / (float) entryCount;

                // Create a LimitLine for the average resting heart rate
                LimitLine avgRestingLine = new LimitLine(avgRestingHeartRate, "Avg Resting HR");
                avgRestingLine.setLineColor(Color.RED);
                avgRestingLine.setLineWidth(2f);

                // Configure the bar chart
                BarDataSet dataSet = new BarDataSet(entries, "Heart Rate (BPM)");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                BarData barData = new BarData(dataSet);
                barChart.setData(barData);

                YAxis yAxis = barChart.getAxisLeft();
                yAxis.setAxisMaximum(0f);
                yAxis.setAxisMaximum(150f);
                yAxis.setAxisLineWidth(1f);
                yAxis.setAxisLineColor(Color.BLACK);
                yAxis.setLabelCount(10);

                barChart.getDescription().setEnabled(false);
                barChart.setBackgroundColor(Color.WHITE);
                barChart.getXAxis().setTextColor(Color.BLACK);
                barChart.getAxisLeft().setTextColor(Color.BLACK);
                barChart.getAxisRight().setEnabled(false);

                // Add the average resting line to the chart
                barChart.getAxisLeft().addLimitLine(avgRestingLine);

                barChart.invalidate();

                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getTimestamps(entries)));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String[] getTimestamps(ArrayList<BarEntry> entries) {
        String[] timestamps = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            String data = entries.get(i).getData().toString();
            String[] parts = data.split(" - ");
            timestamps[i] = parts[0]; // Extract only the recording start time
        }
        return timestamps;
    }





 */


/*
    private String[] getTimestamps(ArrayList<BarEntry> entries) {
        String[] timestamps = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            timestamps[i] = entries.get(i).getData().toString();
        }
        return timestamps;
    }

 */





/*

    private void fetchSensorsData2() {
    //only fetches avgs of all test results (displays avg of low, max, resting)
    //ignore this

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
/*
csv final
 */

    public void exportDataToCSV(View view) {
        // Retrieve the employee data
        String empName = empNameRec.getText().toString();
        String empId = empIddata.getText().toString();
        String age = agedata.getText().toString();
        String height = heightdata.getText().toString();
        String weight = weightdata.getText().toString();
        String deviceId = deviceIDdata.getText().toString();

        // Retrieve the sensor data
        String low = tvLow.getText().toString();
        String resting = tvResting.getText().toString();
        String max = tvMax.getText().toString();

        String avgLow = AvgtvLow.getText().toString();
        String avgResting = AvgtvResting.getText().toString();
        String avgMax = AvgtvMax.getText().toString();

        // Determine the pass/fail test result
        String passFailResult = "Employee Failed E.H.T.S Exam"; // Set the initial result as failed
        if (cardFinalResult.getCardBackgroundColor().getDefaultColor() == Color.GREEN) {
            passFailResult = "Employee Passed E.H.T.S Exam";
        }

        // Create a CSV record using OpenCSV library
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"EHTS TEST RESULTS"});
        rows.add(new String[]{"Employee Name:", empName});
        rows.add(new String[]{"Employee ID:", empId});
        rows.add(new String[]{"Age:", age});
        rows.add(new String[]{"Height (IN):", height});
        rows.add(new String[]{"Weight (LB):", weight});
        rows.add(new String[]{"Device ID:", deviceId});
        rows.add(new String[]{});
        rows.add(new String[]{"Last Heart Rate Results"});
        rows.add(new String[]{"Low:", low});
        rows.add(new String[]{"Resting:", resting});
        rows.add(new String[]{"Max:", max});
        rows.add(new String[]{});
        rows.add(new String[]{"Avg. Heart Rate Results of All Tests"});
        rows.add(new String[]{"Low:", avgLow});
        rows.add(new String[]{"Resting:", avgResting});
        rows.add(new String[]{"Max:", avgMax});
        rows.add(new String[]{});
        rows.add(new String[]{"Pass/Fail Test Results:", passFailResult});

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference heartRateRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("employees").child(empId).child("sensors_record").child(empId);

        Query query = heartRateRef.orderByChild("recordingStartTime");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int testNo = 1;
                    rows.add(new String[]{});
                    rows.add(new String[]{"Timeline Test Results:"});
                    rows.add(new String[]{"Test No.", "Test Start Time:", "Test End Time:", "Low HR", "Resting HR", "Max HR"});
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                        String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                        Integer low = childSnapshot.child("low").getValue(Integer.class);
                        Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                        Integer max = childSnapshot.child("max").getValue(Integer.class);

                        rows.add(new String[]{
                                String.valueOf(testNo),
                                convertToTimestamp1(recordingStartTimestamp),
                                convertToTimestamp1(recordingStopTimestamp),
                                String.valueOf(low),
                                String.valueOf(resting),
                                String.valueOf(max)
                        });

                        testNo++;
                    }

                    /*
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String recordingStartTimestamp = childSnapshot.child("recordingStartTime").getValue(String.class);
                        String recordingStopTimestamp = childSnapshot.child("recordingStopTime").getValue(String.class);
                        Integer low = childSnapshot.child("low").getValue(Integer.class);
                        Integer resting = childSnapshot.child("resting").getValue(Integer.class);
                        Integer max = childSnapshot.child("max").getValue(Integer.class);

                        rows.add(new String[]{
                                String.valueOf(testNo),
                                recordingStartTimestamp,
                                recordingStopTimestamp,
                                String.valueOf(low),
                                String.valueOf(resting),
                                String.valueOf(max)
                        });

                        testNo++;
                    }

                     */

                    // Generate a unique filename for the CSV file
                 //   String filename = "employee_data_" + System.currentTimeMillis() + ".csv";

                    String timestamp = new SimpleDateFormat("yyyy_MM_dd_hh_mm", Locale.US).format(new Date());
                    String filename = "employee_data_" + timestamp + ".csv";

                    try {
                        // Create the CSV file in the app's cache directory
                        File cacheDir = getCacheDir();
                        File csvFile = new File(cacheDir, filename);

                        // Write the data to the CSV file
                        FileWriter writer = new FileWriter(csvFile);
                        CSVWriter csvWriter = new CSVWriter(writer);
                        csvWriter.writeAll(rows);
                        csvWriter.close();

                        // Share the CSV file using Intent
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/csv");
                        Uri fileUri = FileProvider.getUriForFile(EmployeeRecord.this, getPackageName() + ".fileprovider", csvFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        startActivity(Intent.createChooser(shareIntent, "Export CSV"));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(EmployeeRecord.this, "Failed to export data to CSV.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error case
                Toast.makeText(EmployeeRecord.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertToTimestamp(float hoursSinceCustomEpoch) {
        long milliseconds = (long) (hoursSinceCustomEpoch * 60 * 60 * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }
    private String convertToTimestamp1(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.US);
            outputFormat.setTimeZone(TimeZone.getDefault()); // Set the correct timezone
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }



    /*
public void exportDataToCSV(View view) {
    // Retrieve the employee data
    String empName = empNameRec.getText().toString();
    String empId = empIddata.getText().toString();
    String age = agedata.getText().toString();
    String height = heightdata.getText().toString();
    String weight = weightdata.getText().toString();
    String deviceId = deviceIDdata.getText().toString();

    // Retrieve the sensor data
    String low = tvLow.getText().toString();
    String resting = tvResting.getText().toString();
    String max = tvMax.getText().toString();

    String avgLow = AvgtvLow.getText().toString();
    String avgResting = AvgtvResting.getText().toString();
    String avgMax = AvgtvMax.getText().toString();

    // Determine the pass/fail test result
    String passFailResult = "Employee Failed E.H.T.S Exam"; // Set the initial result as failed
    if (cardFinalResult.getCardBackgroundColor().getDefaultColor() == Color.GREEN) {
        passFailResult = "Employee Passed E.H.T.S Exam";
    }

    // Create a CSV record using OpenCSV library
    List<String[]> rows = new ArrayList<>();
    rows.add(new String[]{"EHTS TEST RESULTS"});
    rows.add(new String[]{"Employee Name:", empName});
    rows.add(new String[]{"Employee ID:", empId});
    rows.add(new String[]{"Age:", age});
    rows.add(new String[]{"Height (IN):", height});
    rows.add(new String[]{"Weight (LB):", weight});
    rows.add(new String[]{"Device ID:", deviceId});
    rows.add(new String[]{});
    rows.add(new String[]{"Last Heart Rate Results"});
    rows.add(new String[]{"Low:", low});
    rows.add(new String[]{"Resting:", resting});
    rows.add(new String[]{"Max:", max});
    rows.add(new String[]{
,
    });
    rows.add(new String[]{"Avg. Heart Rate Results of All Tests"});
    rows.add(new String[]{"Low:", avgLow});
    rows.add(new String[]{"Resting:", avgResting});
    rows.add(new String[]{"Max:", avgMax});
    rows.add(new String[]{
,

    });
    rows.add(new String[]{"Pass/Fail Test Results:", passFailResult});

    // Generate a unique filename for the CSV file
    String filename = "employee_data_" + System.currentTimeMillis() + ".csv";

    try {
        // Create the CSV file in the app's cache directory
        File cacheDir = getCacheDir();
        File csvFile = new File(cacheDir, filename);

        // Write the data to the CSV file
        FileWriter writer = new FileWriter(csvFile);
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeAll(rows);
        csvWriter.close();

        // Share the CSV file using Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/csv");
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", csvFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(Intent.createChooser(shareIntent, "Export CSV"));

    } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(EmployeeRecord.this, "Failed to export data to CSV.", Toast.LENGTH_SHORT).show();
    }
}


     */






}