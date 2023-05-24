package com.ehts.smartwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginwatch extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button bt_Login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwatch);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.emailAddress);
        editPassword = findViewById(R.id.password);
        bt_Login = findViewById(R.id.login);

        // Set up the OnEditorActionListener for the password EditText to capture the keyboard "Done" action
        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform the login action when the "Done" action is triggered on the keyboard
                    login();
                    return true;
                }
                return false;
            }
        });

        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        // Check if email/password is empty
        if (email.isEmpty()) {
            Toast.makeText(loginwatch.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(loginwatch.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(loginwatch.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Start the desired activity for Wear OS, e.g., displaying heart rate data
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(loginwatch.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
