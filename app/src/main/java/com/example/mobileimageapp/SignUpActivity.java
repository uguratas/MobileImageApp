package com.example.mobileimageapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private FireBaseCollect fireBaseCollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        fireBaseCollect = new FireBaseCollect();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    registerUser(username, password);
                } else {
                    Toast.makeText(SignUpActivity.this, "Lütfen kullanıcı adı ve şifre girin.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String username, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);

        fireBaseCollect.addDataToFirestore(username, user);

        Toast.makeText(SignUpActivity.this, "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
