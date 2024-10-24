package com.example.mobileimageapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        // SharedPreferences'ten tüm verileri al
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = preferences.getAll();

        // Bu verileri ekranda göster
        TextView debugTextView = findViewById(R.id.debugTextView);

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            debugTextView.append(entry.getKey() + ": " + entry.getValue().toString() + "\n");
        }
    }
}
