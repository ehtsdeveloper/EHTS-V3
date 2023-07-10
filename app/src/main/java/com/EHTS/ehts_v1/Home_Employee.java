package com.EHTS.ehts_v1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Home_Employee extends AppCompatActivity {

    ImageButton menubutton, addEmployeeButton;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<Profile_Data> dataList;
    MyAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home__employee);

        menubutton = findViewById(R.id.backButton);

        addEmployeeButton = findViewById(R.id.addEmployeeButton);
        recyclerView = findViewById(R.id.recyclerview);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Home_Employee.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Home_Employee.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(Home_Employee.this, dataList);
        recyclerView.setAdapter(adapter);


        // databaseReference = FirebaseDatabase.getInstance().getReference("employees");
        // Get the user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("employees");

        dialog.show();
        // dialog.show();


        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Profile_Data data = itemSnapshot.getValue(Profile_Data.class);
                    data.setKey(itemSnapshot.getKey());
                    dataList.add(data);

                    //saveDataToEmployee(dataClass.getEmpId(), "employees");
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });


        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Go back to the previous page
            }
        });
        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle add employee button click
                Intent addEmployeeIntent = new Intent(Home_Employee.this, Upload_Profile.class);
                startActivity(addEmployeeIntent);
            }
        });



    }
    public void searchList(String text){
        ArrayList<Profile_Data> searchList = new ArrayList<>();
        for (Profile_Data dataClass: dataList){
            if (dataClass.getDataName().toLowerCase().contains(text.toLowerCase())
                    || dataClass.getDataEmpID().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }

}
