package com.EHTS.ehts_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    //create objects
    TextView backToLoginPage;

    TextInputEditText editEmail, editPassword;
    Button bt_reset;
    FirebaseAuth mAuth;
     ProgressBar progressBar;
    TextView bkToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);



        //initialize
        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.emailAddress);
        //editPassword = findViewById(R.id.password);
        bt_reset = findViewById(R.id.resetbtn);
        progressBar = findViewById(R.id.progressBar);
        bkToLogin = findViewById(R.id.BacktoLogin);


        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                resetpassword();
            }


        });

        bkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });



    }
    private void resetpassword() {
        String email = editEmail.getText().toString().trim();

        if(email.isEmpty()){
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please Provide a Valid Email Address!");
            editEmail.requestFocus();
            return;
        }


        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){
                        Toast.makeText(ForgotPassword.this, "Check your Email to reset your Password!", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(ForgotPassword.this, "Something went wrong. Try again!", Toast.LENGTH_LONG).show();
                    }
            }
        });
    }
}