package com.example.mobileimageapp;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class FireBaseCollect {
    private static final String TAG = "FireBaseCollect";
    private FirebaseFirestore db;

    public FireBaseCollect() {
        // Firebase Firestore'a bağlanma
        db = FirebaseFirestore.getInstance();
    }

    public void addDataToFirestore(String username, Map<String, Object> data) {
        db.collection("userDatabase").document(username).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Belge başarıyla eklendi");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Belge eklenirken hata oluştu", e);
                    }
                });
    }

    public Task<DocumentSnapshot> getDataFromFirestore(String username) {
        return db.collection("userDatabase").document(username).get();
    }
    public Task<DocumentSnapshot> getDataFromFirebaseValue() {
        return db.collection("myCollection").document("myDocument").get();
    }

}
