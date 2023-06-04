package com.EHTS.ehts_v1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadProfileViewModel extends AndroidViewModel {

    private DatabaseReference databaseReference;

    public UploadProfileViewModel(@NonNull Application application) {
        super(application);
        databaseReference = FirebaseDatabase.getInstance().getReference("EHTS");
    }

    public LiveData<Boolean> saveProfileData(Profile_Data data) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

        // Generate a unique key for the data
        String key = databaseReference.push().getKey();

        if (key != null) {
            databaseReference.child(key)
                    .setValue(data)
                    .addOnCompleteListener(task -> successLiveData.setValue(task.isSuccessful()))
                    .addOnFailureListener(e -> successLiveData.setValue(false));
        } else {
            successLiveData.setValue(false);
        }

        return successLiveData;
    }
}
