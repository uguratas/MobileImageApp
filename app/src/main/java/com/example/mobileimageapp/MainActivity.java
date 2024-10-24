package com.example.mobileimageapp;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Button captureImageButton;
    public static final int REQUEST_IMAGE_CAPTURE = 33;
    private Button loginButton;
    private Button selectImageButton;
    private ImageView selectedImageView;
    public static final int REQUEST_IMAGE_PICK = 2;
    public static final String SELECTED_IMAGE_URI = "selected_image_uri";
    private String currentPhotoPath;
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        selectImageButton = findViewById(R.id.selectImageButton);
        //selectedImageView = findViewById(R.id.selectedImageView);



        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchPickImageIntent();
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("username");
                editor.remove("password");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");
        String savedPassword = preferences.getString("password", "");

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        // Kaydedilen resmi galeriden getir ve göster
        //Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
        //    startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
       // }
    }




    private void dispatchPickImageIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                openDisplayImageActivity(selectedImageUri);
            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Kamera ile çekilen fotoğrafın URI'sini al
            Uri capturedImageUri = Uri.fromFile(new File(currentPhotoPath));
            // DisplayCapturedImageActivity'yi başlat
            //openDisplayCapturedImageActivity(capturedImageUri);
        }else {
            // Hata durumunda kullanıcıya bilgi ver
            Toast.makeText(this, "Beklenmeyen bir durum oluştu.", Toast.LENGTH_SHORT).show();
        }

    }





    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        try {
            File imageFile = createImageFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createImageFile() throws IOException {
        // Resim dosyasını oluştur
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Oluşturulan dosyanın URI'sini döndür
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private void openDisplayImageActivity(Uri imageUri) {
        if (imageUri != null) {
            Intent intent = new Intent(this, DisplayImageActivity.class);
            intent.putExtra(SELECTED_IMAGE_URI, imageUri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Resim seçilemedi.", Toast.LENGTH_SHORT).show();
        }
    }
}