package com.EHTS.ehts_v1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

//never completed this feature - it was an extra feature I personally wanted to add not important
//allows user to edit their profiles within the employee record screeen

public class Update_Profile extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
     DatabaseReference databaseReference;
     StorageReference storageReference;

    ImageView updateImage;
    TextView textViewUpdate;

    Button updateButton;

    EditText updateName, updateEmpID, updateAge, updateHeight, updateWeight, updategender;

    Uri updateImageUri;

    ProgressBar progressLayout;

    ImageButton backButton;
    String key = "";
    String oldImageURL;
    String imageUrl = "";
   // String name, age, height, weight, gender;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.GONE);
        backButton = findViewById(R.id.backButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Update_Profile.this, Home_Employee.class);
                startActivity(intent);
               // onBackPressed(); // Go back to the previous page
            }
        });


        updateImage = findViewById(R.id.updateImage);
        textViewUpdate = findViewById(R.id.textviewUpdate);
        updateButton = findViewById(R.id.updateButton);
        updateName = findViewById(R.id.updateName);
        updateEmpID = findViewById(R.id.updateEmpID);
        updateAge = findViewById(R.id.updateAge);
        updateHeight = findViewById(R.id.updateHeight);
        updateWeight = findViewById(R.id.updateWeight);
        updategender = findViewById(R.id.updategender);

//comeback if any issues
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        updateImageUri = data.getData();
                        updateImage.setImageURI(updateImageUri);
                    } else {
                        Toast.makeText(Update_Profile.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            updateName.setText(bundle.getString("Employee Name"));
            updateEmpID.setText(bundle.getString("Employee ID"));
            updateAge.setText(bundle.getString("Age"));
            updateHeight.setText(bundle.getString("Height (in)"));
            updateWeight.setText(bundle.getString("Weight (lb)"));
            updategender.setText(bundle.getString("Gender"));


            key = bundle.getString("Key");
            oldImageURL = bundle.getString("images/");
            Glide.with(Update_Profile.this).load(bundle.getString("images/")).into(updateImage);
        }

        // Retrieve the current user's ID
      //  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get the employee ID
        //String employeeId = updateEmpID.getText().toString();


       // databaseReference.child("users").child(userId).child("employees").child(employeeId).child(key);

        textViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setType("image/*");
                galleryLauncher.launch(iGallery);
            }
        });



        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Update_Profile.this, Home_Employee.class);
                        startActivity(intent);
                saveData();

            }


        });
       // EditText updateName, updateEmpID, updateAge, updateHeight, updateWeight, updategender;
    }
    private void saveData() {

        if (updateImageUri != null) {
            final String name = updateName.getText().toString();
            final String empId = updateEmpID.getText().toString();
            final int age;
            final int height;
            final int weight;
            final String gender = updategender.getText().toString();

            try {
                age = Integer.parseInt(updateAge.getText().toString());
                height = Integer.parseInt(updateHeight.getText().toString());
                weight = Integer.parseInt(updateWeight.getText().toString());

            } catch (NumberFormatException e) {
                // Handle the case when the input cannot be parsed as an integer
                Toast.makeText(Update_Profile.this, "Invalid input for age, height, or weight", Toast.LENGTH_SHORT).show();
                return;
            }

           // String imageFileName = UUID.randomUUID().toString(); // Generate a unique filename for the image
           // String imageFileName = key; // Use the existing key as the filename for the image
            //storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageFileName);
            storageReference = FirebaseStorage.getInstance().getReference().child("images/" ).child(updateImageUri.getLastPathSegment());
            // Show the progress bar
            progressLayout.setVisibility(View.VISIBLE);

            // Upload the image to Firebase Storage
            storageReference.putFile(updateImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    imageUrl = downloadUri.toString();

                                    // Create a new Profile_Data object with the download URL and other data
                                    Profile_Data data = new Profile_Data(name, empId, age, height, weight, gender, imageUrl);

                                    //We are changing the child from empid to currentDate, (may have an issue here comeback!!!!)
                                    // because we will be updating empid as well and it may affect child value.
                                    String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


                                    // Retrieve the current user's ID
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    // Get the employee ID
                                    String employeeId = updateEmpID.getText().toString();

                                    // references "employees" node in database
                                   // databaseReference.child("users").child(userId).child("employees").child(employeeId).setValue(data)
                                   databaseReference.child("users").child(userId).child(currentDate).child("employees").child(employeeId).child(key).setValue(data)
                                            //databaseReference.child("users").child(userId).child("employees").child(dataEmpID.getText().toString()).setValue(data)
                                    //databaseReference.child("employees").child(key).setValue(data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Update_Profile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        deleteOldImage();
                                                    } else {
                                                        Toast.makeText(Update_Profile.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            /*
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Hide the progress bar
                                                    progressLayout.setVisibility(View.GONE);
                                                    Toast.makeText(Update_Profile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                                    // Reset input fields and image
                                                   // updateName.setText("");
                                                   // updateEmpID.setText("");
                                                   // updateAge.setText("");
                                                   // updateHeight.setText("");
                                                   // updateWeight.setText("");
                                                   // updategender.setText("");
                                                   // updateImage.setImageResource(R.drawable.baseline_add_a_photo_24);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Hide the progress bar
                                                    progressLayout.setVisibility(View.GONE);
                                                    Toast.makeText(Update_Profile.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                             */
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Update_Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Update_Profile.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }


    }

    private void deleteOldImage() {
        if (oldImageURL != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
            storageReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Old image deleted successfully
                            Toast.makeText(Update_Profile.this, "Old image deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete old image
                            Toast.makeText(Update_Profile.this, "Failed to delete old image", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }



}