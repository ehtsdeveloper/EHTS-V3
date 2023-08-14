package com.ehts.ehtswatch

import android.app.Activity
import android.os.Bundle
import android.content.Intent

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth


class loginwatch : Activity() {

        private lateinit var editEmail: EditText
        private lateinit var editPassword: EditText
        private lateinit var bt_Login: Button
        private lateinit var mAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_loginwatch)

            // Initialize Firebase Authentication
            mAuth = FirebaseAuth.getInstance()
            editEmail = findViewById(R.id.emailAddress)
            editPassword = findViewById(R.id.password)
            bt_Login = findViewById(R.id.login)

            // Set up the OnEditorActionListener for the password EditText to capture the keyboard "Done" action
            editPassword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Perform the login action when the "Done" action is triggered on the keyboard
                    login()
                    return@setOnEditorActionListener true
                }
                false
            }
            bt_Login.setOnClickListener { login() }
        }

        private fun login() {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            // Check if email/password is empty
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return
            }
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        // Start the desired activity for Wear OS, e.g., displaying heart rate data
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
}
