package com.example.mobileimageapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.text.DecimalFormat;

// HistogramActivity.java
public class HistogramActivity extends AppCompatActivity {
    private static final String TAG = "HistogramActivity";
    private TextView histogramValuesTextView;
    private HistogramView histogramView;
    private TextView firebaseDataTextView;
    private int[] histogramData;
    private FireBaseCollect fireBaseCollect;
    private List<Double> firebaseDataList;
    private TextView weightedSumTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);

        //Spinner groupSizeSpinner = findViewById(R.id.groupSizeSpinner);
        histogramValuesTextView = findViewById(R.id.histogramValuesTextView);
        histogramView = findViewById(R.id.histogramView);
        firebaseDataTextView = findViewById(R.id.firebaseDataTextView);
        weightedSumTextView = findViewById(R.id.weightedSumTextView);

        // Firestore sınıfının bir örneğini oluştur
        fireBaseCollect = new FireBaseCollect();

        // Histogram verilerini al
        histogramData = getIntent().getIntArrayExtra("histogram_data");

        // Directly update histogram with a default group size
        int defaultGroupSize = 64; // Set your desired default group size here
        updateHistogram(defaultGroupSize);
        // Firebase'den verileri al ve ekrana yazdır
        getDataFromFirebase();

        // Geri dönüş butonunu bul ve onClickListener ekleyerek geri dönme işlemini ekle
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aktiviteyi sonlandırarak önceki aktiviteye geri dön
                finish();
            }
        });
    }

    private void updateHistogram(int groupSize) {
        groupSize = 64;
        // Histogram görüntüsünü güncelle
        histogramView.setHistogramData(groupHistogram(histogramData, groupSize));

        // Histogram değerlerini TextView'e yazdır
        updateHistogramValuesTextView(groupSize);
    }

    private void updateHistogramValuesTextView(int groupSize) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalSum = 0;
        for (int i = 0; i < histogramData.length; i += groupSize) {
            int sum = 0;
            for (int j = i; j < Math.min(i + groupSize, histogramData.length); j++) {
                sum += histogramData[j];
            }
            stringBuilder.append((i / groupSize)).append(" Bin ").append(": ").append(sum).append("\n");
        }

        histogramValuesTextView.setText(stringBuilder.toString());

        // Firebase verileriyle histogram verilerini çarparak topla
        calculateWeightedSum();
    }

    private void calculateWeightedSum() {
        if (firebaseDataList != null) {
            String histogramText = histogramValuesTextView.getText().toString();
            String[] lines = histogramText.split("\n");
            double weightedSum = 0;

            for (String line : lines) {
                if (line.contains("Bin")) {
                    String[] parts = line.split(": ");
                    int binIndex = Integer.parseInt(parts[0].split(" ")[0]);
                    int binValue = Integer.parseInt(parts[1].trim());

                    if (binIndex < firebaseDataList.size()) {
                        weightedSum += binValue * firebaseDataList.get(binIndex);
                    }
                }
            }
            weightedSumTextView.setText("ESTIMATED MILLITER VALUE: " + weightedSum);
        } else {
            weightedSumTextView.setText("ESTIMATED MILLITER VALUE: Veriler bekleniyor...");
        }
    }
    private int[] groupHistogram(int[] histogram, int groupSize ) {
        // Histogram dizisini gruplandırmak için yeni bir dizi oluştur
        int numGroups = (int) Math.ceil((double) histogram.length / groupSize);
        int[] groupedHistogram = new int[numGroups];

        // Her grup için histogram değerlerini topla
        for (int i = 0; i < histogram.length; i++) {
            int groupIndex = i / groupSize;
            groupedHistogram[groupIndex] += histogram[i];
        }

        return groupedHistogram;
    }
    private void getDataFromFirebase() {
        fireBaseCollect.getDataFromFirebaseValue().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null) {
                        TreeMap<String, Object> sortedData = new TreeMap<>(data);
                        StringBuilder dataString = new StringBuilder();
                        firebaseDataList = new ArrayList<>();
                        DecimalFormat decimalFormat = new DecimalFormat("#.######"); // 6 decimal places
                        for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
                            double value = Double.parseDouble(entry.getValue().toString());
                            dataString.append(entry.getKey()).append(": ").append(decimalFormat.format(value)).append("\n");
                            firebaseDataList.add(value);
                        }
                        firebaseDataTextView.setText(dataString.toString());
                        // Firebase verileri alındığında weighted sum'ı hesapla
                        calculateWeightedSum();
                    }
                } else {
                    Log.d(TAG, "Belge bulunamadı.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Belge alınırken hata oluştu", e);
            }
        });
    }
}


