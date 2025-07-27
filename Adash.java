package com.example.sympto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Adash extends AppCompatActivity {

    private Button btnManageUser, btnHospital, btnManageDisease, btnViewRep, btnViewMed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adash); // Make sure adash.xml exists in res/layout

        // Initialize buttons
        btnManageUser = findViewById(R.id.btnManageUser);
        btnHospital = findViewById(R.id.btnHospital);
        btnManageDisease = findViewById(R.id.btnManageDisease);
        btnViewRep = findViewById(R.id.btnViewRep);
        btnViewMed = findViewById(R.id.btnViewMed);

        // Set click listeners for each button
        btnManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Manage Users Clicked");
            }
        });

        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Update Hospital And Doctor Details Clicked");
            }
        });

        btnManageDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Manage Disease Information Clicked");
            }
        });

        btnViewRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("View Reports Clicked");
            }
        });

        btnViewMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("View Medicine Reminders Clicked");
            }
        });
    }

    // Function to show a toast message
    private void showToast(String message) {
        Toast.makeText(Adash.this, message, Toast.LENGTH_SHORT).show();
    }
}
