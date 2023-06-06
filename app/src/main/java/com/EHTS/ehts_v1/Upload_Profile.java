package com.EHTS.ehts_v1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;


import java.util.UUID;

public class Upload_Profile extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
    private DatabaseReference databaseReference;

    ImageView dataImage;
    TextView textView;

    Button saveButton;

    EditText dataName, dataEmpID, dataAge, dataHeight, dataWeight, dataDeviceID;

    Uri selectedImageUri;

    ProgressBar progressLayout;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.GONE);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Go back to the previous page
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        dataImage.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(Upload_Profile.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dataImage = findViewById(R.id.uploadImage);
        textView = findViewById(R.id.textviewUpload);
        saveButton = findViewById(R.id.uploadButton);
        dataName = findViewById(R.id.uploadName);
        dataEmpID = findViewById(R.id.uploadEmpID);
        dataAge = findViewById(R.id.uploadAge);
        dataHeight = findViewById(R.id.uploadHeight);
        dataWeight = findViewById(R.id.uploadWeight);
        dataDeviceID = findViewById(R.id.uploadDeviceID);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setType("image/*");
                galleryLauncher.launch(iGallery);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();

            }
        });
    }

    public void saveData() {
        if (selectedImageUri != null) {
            final String name = dataName.getText().toString();
            final String empId = dataEmpID.getText().toString();
            final int age;
            final int height;
            final int weight;
            final String deviceID = dataDeviceID.getText().toString();

            try {
                age = Integer.parseInt(dataAge.getText().toString());
                height = Integer.parseInt(dataHeight.getText().toString());
                weight = Integer.parseInt(dataWeight.getText().toString());
            } catch (NumberFormatException e) {
                // Handle the case when the input cannot be parsed as an integer
                Toast.makeText(Upload_Profile.this, "Invalid input for age, height, or weight", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageFileName = UUID.randomUUID().toString(); // Generate a unique filename for the image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageFileName);

            // Show the progress bar
            progressLayout.setVisibility(View.VISIBLE);

            // Upload the image to Firebase Storage
            storageReference.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {

                                    // Create a new Profile_Data object with the download URL and other data
                                    Profile_Data data = new Profile_Data(name, empId, age, height, weight, deviceID, downloadUri.toString());

                                    // Retrieve the current user's ID
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    // Assuming you have a "profiles" node in your database
                                    databaseReference.child("users").child(userId).child("employees").child(dataEmpID.getText().toString()).setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Hide the progress bar
                                                    progressLayout.setVisibility(View.GONE);
                                                    Toast.makeText(Upload_Profile.this, "Saved", Toast.LENGTH_SHORT).show();
                                                    // Reset input fields and image
                                                    dataName.setText("");
                                                    dataEmpID.setText("");
                                                    dataAge.setText("");
                                                    dataHeight.setText("");
                                                    dataWeight.setText("");
                                                    dataDeviceID.setText("");
                                                    dataImage.setImageResource(R.drawable.baseline_add_a_photo_24);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Hide the progress bar
                                                    progressLayout.setVisibility(View.GONE);
                                                    Toast.makeText(Upload_Profile.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Upload_Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Upload_Profile.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }



}
