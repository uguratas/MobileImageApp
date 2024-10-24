package com.example.mobileimageapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.io.InputStream;

public class DisplayImageActivity extends AppCompatActivity {

    private static final String TAG = "DisplayImageActivity";
    private ImageView imageView;
    private float currentRotation = 0;

    // Add a variable to store the selected image URI
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        imageView = findViewById(R.id.displayImageView);
        Button rotateRightButton = findViewById(R.id.rotateRightButton);
        Button rotateLeftButton = findViewById(R.id.rotateLeftButton);
        Button grayscaleButton = findViewById(R.id.grayscaleButton);
        TextView imagePathTextView = findViewById(R.id.imagePathTextView);
        Button selectRedButton = findViewById(R.id.selectRedButton);


        // Seçilen resmin URI'sini al
        selectedImageUri = getIntent().getParcelableExtra(MainActivity.SELECTED_IMAGE_URI);

        // ImageView'e seçilen resmi yerleştir
        imageView.setImageURI(selectedImageUri);

        // Get additional information about the selected image
        String imagePath = getImagePath(selectedImageUri);
        Log.d(TAG, "Image Path: " + imagePath);


        // Set the text with the image path
        if (imagePath != null) {
            imagePathTextView.setText("Image Path: " + imagePath + "\n" +
                    "Image Dimensions: " + getImageDimensions(selectedImageUri));
        } else {
            imagePathTextView.setText("Image Path: Camera"+ "\n" +
                    "Image Dimensions: " + getImageDimensions(selectedImageUri));
        }

        selectRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuttingRedToGray();
            }
        });

        grayscaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyGrayscale();
            }
        });
        // Set onClickListener for the rotate right button
        rotateRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(90); // Rotate right (clockwise)
            }
        });

        // Set onClickListener for the rotate left button
        rotateLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(-90); // Rotate left (counterclockwise)
            }
        });
    }


    private String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }



    private String getImageDimensions(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(inputStream, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return width + " x " + height;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private void rotateImage(float degrees) {
        // Increment rotation angle by the specified degrees
        currentRotation += degrees;

        // Apply rotation to the ImageView
        imageView.setRotation(currentRotation);
    }
    private Bitmap originalBitmap;
    private Bitmap grayscaleBitmap;
    private boolean isGrayscale = false;
    private void applyGrayscale() {
        // ImageView'dan Bitmap'i al
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bitmapDrawable != null) {
            if (originalBitmap == null) {
                originalBitmap = bitmapDrawable.getBitmap();
            }

            if (isGrayscale) {
                imageView.setImageBitmap(originalBitmap);
                isGrayscale = false;
            } else {
                if (grayscaleBitmap == null) {
                    grayscaleBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                            originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                }

                // Gri tonlamaya çevirmek için bir ColorMatrix oluştur
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);

                // Gri tonlamayı uygulamak için ColorMatrixColorFilter oluştur
                ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

                // Yeni bir Paint oluştur ve color filter'ı uygula
                Paint paint = new Paint();
                paint.setColorFilter(colorFilter);

                // Orijinal Bitmap'i, yeni gri tonlu Bitmap üzerine çiz
                Canvas canvas = new Canvas(grayscaleBitmap);
                canvas.drawBitmap(originalBitmap, 0, 0, paint);

                // ImageView'e gri tonlu Bitmap'i uygula
                imageView.setImageBitmap(grayscaleBitmap);

                isGrayscale = true;
            }
        }
    }
    /**private void applyRedHistogram() {
        // ImageView'dan Bitmap'i al
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap originalBitmap = bitmapDrawable.getBitmap();

            // Yeni bir Bitmap oluştur (Aynı boyutlarda ve ARGB_8888 formatında)
            Bitmap redBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                    originalBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);

            // Her pikseli tarayarak kırmızı tonları belirle
            for (int y = 0; y < originalBitmap.getHeight(); y++) {
                for (int x = 0; x < originalBitmap.getWidth(); x++) {
                    // Pikselin ARGB değerlerini al
                    int pixel = originalBitmap.getPixel(x, y);
                    int alpha = Color.alpha(pixel);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    // Pikselin kırmızı değerini diğer bileşenlerle karşılaştırarak belirliyoruz
                    // Eğer kırmızı değer diğer bileşenlerden büyükse ve fark belirli bir eşik değerinden büyükse, bu pikseli kırmızı olarak işaretle
                    int threshold = 50; // Eşik değeri, ihtiyaca göre ayarlanabilir
                    if (red > green + threshold && red > blue + threshold) {
                        redBitmap.setPixel(x, y, Color.argb(alpha, red, green, blue));
                    } else {
                        // Eğer kırmızı değer diğer bileşenlerden belirgin şekilde farklı değilse, bu pikseli siyah yap
                        redBitmap.setPixel(x, y, Color.BLACK);
                    }
                }
            }

            // Kırmızı bölgeleri griye dönüştür
            Bitmap grayscaleBitmap = toGrayscale(redBitmap);

            // ImageView'e kırmızı bölgeleri vurgulayan ve griye dönüştürülen yeni Bitmap'i uygula
            imageView.setImageBitmap(grayscaleBitmap);

           int[] histogram = calculateHistogram(grayscaleBitmap);

            // Histogram aktivitesine geçiş yap ve histogram verilerini aktar
            Intent intent = new Intent(this, HistogramActivity.class);
            intent.putExtra("histogram_data", histogram);
            intent.putExtra("red_bitmap", grayscaleBitmap); // Kırmızı bölgeleri vurgulanmış Bitmap'i aktar
            startActivity(intent);
        }

    }**/


    private void cuttingRedToGray() {
        // ImageView'dan Bitmap'i al
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap originalBitmap = bitmapDrawable.getBitmap();

            // Yeni bir Bitmap oluştur (Aynı boyutlarda ve ARGB_8888 formatında)
            Bitmap redBitmap = Bitmap.createBitmap(originalBitmap.getWidth(),
                    originalBitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);

            // Her pikseli tarayarak kırmızı tonları belirle
            for (int y = 0; y < originalBitmap.getHeight(); y++) {
                for (int x = 0; x < originalBitmap.getWidth(); x++) {
                    // Pikselin ARGB değerlerini al
                    int pixel = originalBitmap.getPixel(x, y);
                    int alpha = Color.alpha(pixel);
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);

                    // Pikselin kırmızı değerini diğer bileşenlerle karşılaştırarak belirliyoruz
                    // Eğer kırmızı değer diğer bileşenlerden büyükse ve fark belirli bir eşik değerinden büyükse, bu pikseli kırmızı olarak işaretle
                    int threshold = 50; // Eşik değeri
                    if (red > green + threshold && red > blue + threshold) {
                        redBitmap.setPixel(x, y, Color.argb(alpha, red, green, blue));
                    } else {
                        // Eğer kırmızı değer diğer bileşenlerden belirgin şekilde farklı değilse, bu pikseli siyah yap
                        redBitmap.setPixel(x, y, Color.BLACK);
                    }
                }
            }

            // Kırmızı bölgeleri griye dönüştür
            Bitmap grayscaleBitmap = toGrayscale(redBitmap);

            // ImageView'e kırmızı bölgeleri vurgulayan ve griye dönüştürülen yeni Bitmap'i uygula
            imageView.setImageBitmap(grayscaleBitmap);

            // Select Red to Gray butonunu güncelle
            Button selectRedButton = findViewById(R.id.selectRedButton);
            selectRedButton.setText("View Histogram");
            selectRedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // HistogramActivity'e geçiş yap
                    int[] histogram = calculateHistogram(grayscaleBitmap);
                    Intent intent = new Intent(DisplayImageActivity.this, HistogramActivity.class);
                    intent.putExtra("histogram_data", histogram);
                    startActivity(intent);
                }
            });
        }
    }

    private int[] groupHistogram(int[] histogram, int groupSize) {
        // Histogram dizisini gruplandırmak için yeni bir dizi oluştur
        int numGroups = histogram.length / groupSize;
        int[] groupedHistogram = new int[numGroups];

        // Her grup için histogram değerlerini topla
        for (int i = 0; i < numGroups; i++) {
            int groupValue = 0;
            for (int j = i * groupSize; j < (i + 1) * groupSize; j++) {
                groupValue += histogram[j];
            }
            groupedHistogram[i] = groupValue;
        }

        return groupedHistogram;
    }
    private Bitmap toGrayscale(Bitmap bitmap) {
        // Gri tonlamaya çevirmek için bir ColorMatrix oluştur
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        // Gri tonlamayı uygulamak için ColorMatrixColorFilter oluştur
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);

        // Yeni bir Paint oluştur ve color filter'ı uygula
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        // Yeni bir Bitmap oluştur ve gri tonlamayı uygula
        Bitmap grayscaleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayscaleBitmap);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayscaleBitmap;
    }

    private int[] calculateHistogram(Bitmap bitmap) {
        int[] histogram = new int[256]; // 256 gri seviyesi için histogram dizisi
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i, j);
                int gray = Color.red(pixel); // Gri tonlamaya dönüştürüldüğü için sadece kırmızı değer alınır
                histogram[gray]++;
            }
        }
        return histogram;
    }


}
