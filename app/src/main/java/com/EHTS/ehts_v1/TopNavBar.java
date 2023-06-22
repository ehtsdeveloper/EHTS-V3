package com.EHTS.ehts_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TopNavBar extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;
    boolean isDrawerLocked = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_nav_bar);

        drawerLayout= findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN); // Disable drawer closing

        // Add a listener to the drawer layout
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // No action needed
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // No action needed
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Check if the drawer should remain locked
                if (isDrawerLocked) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Check if the drawer is idle
                if (newState == DrawerLayout.STATE_IDLE) {
                    // Check if the drawer should remain locked
                    if (isDrawerLocked) {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    } else {
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }
            }
        });

        // Set the flag to keep the drawer locked
        isDrawerLocked = true;

        // when logging in, take the user to the home page
        Intent homeIntent = new Intent(TopNavBar.this, Home_Employee.class);
        loadActivity(homeIntent);

        /*
        //allow open close menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,  drawerLayout, toolbar, R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(toggle);

        //when logging in take user to the home page
        Intent homeIntent = new Intent(TopNavBar.this, Home_Employee.class);
        loadActivity(homeIntent);
        toggle.syncState();
         */



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               int id = item. getItemId();

               if(id == R.id.home) {
                 //  Toast.makeText(TopNavBar.this, "Home", Toast.LENGTH_SHORT).show();
                   //Home_Employee homeFragment = new Home_Employee();
                  // loadFragment(homeFragment);
                   Toast.makeText(TopNavBar.this, "Home", Toast.LENGTH_SHORT).show();

                   Intent homeIntent = new Intent(TopNavBar.this, Home_Employee.class);
                   loadActivity(homeIntent);

               } else if (id == R.id.addEmployees) {

                   Toast.makeText(TopNavBar.this, "Employees", Toast.LENGTH_SHORT).show();
                   Intent employeesIntent = new Intent(TopNavBar.this, Upload_Profile.class);
                   loadActivity(employeesIntent);

               }else if (id == R.id.bleSettings) {
                   Toast.makeText(TopNavBar.this, "Bluetooth Settings", Toast.LENGTH_SHORT).show();
                   Intent bleSettingsIntent = new Intent(TopNavBar.this, BluetoothSettings.class);
                   loadActivity(bleSettingsIntent);

               }  else if (id == R.id.Logoutbtn) {
                Toast.makeText(TopNavBar.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                logoutUser();
            }
               else {

               }

               drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView = navigationView.getHeaderView(0).findViewById(R.id.userdetails);
            textView.setText(user.getDisplayName());
        }

    }

    @Override
    public void onBackPressed() {
         if (drawerLayout.isDrawerOpen(GravityCompat.START)){
             drawerLayout.closeDrawer(GravityCompat.START);
         }else{
              super.onBackPressed();
         }
    }


    private void loadActivity(Intent intent) {
        startActivity(intent);
    }

    /*
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }



     */



    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}

