package com.example.sympto; // Change this to your package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Alogin extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alogin); // Make sure this XML file is correct

        // Initialize UI components
        usernameInput = findViewById(R.id.Username_input);
        passwordInput = findViewById(R.id.Password_input);
        loginBtn = findViewById(R.id.login_btn);

        // Set login button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Check credentials
        if (username.equals("admin") && password.equals("123456")) {
            // Successful login, navigate to Adash.java
            Intent intent = new Intent(Alogin.this, Adash.class);
            startActivity(intent);
            finish(); // Optional: Closes the login activity
        } else {
            // Show error message
            if (password.length() < 6) {
                if (password.length() < 6) {
                    Toast.makeText(Alogin.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Alogin.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}