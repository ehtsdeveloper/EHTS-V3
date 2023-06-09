package com.ehts.smartwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logoutbtn;
    TextView textView;
    //Button gotoHr;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize
        auth = FirebaseAuth.getInstance();
        logoutbtn = findViewById(R.id.logoutbtn);
        textView= findViewById(R.id.userdetails);
       // gotoHr = findViewById(R.id.gotoHr);

        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), loginwatch.class);
            startActivity(intent);
            finish();
        }
        else{
            textView.setText(user.getEmail());
        }


   /*
        gotoHr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HeartRate_Monitor.class);
                startActivity(intent);
            }
        });

 */
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), loginwatch.class);
                startActivity(intent);
                finish();
            }
        });

    }

}