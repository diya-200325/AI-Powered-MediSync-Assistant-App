package com.example.sympto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvMarqueeTitle;
    private Button btnMedicineReminder, btnSymptomAnalysis, btnVitalMonitoring, btnGenerateReports;
    private TextView tvLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity); // Make sure the XML file name matches

        // Bind views
        tvMarqueeTitle = findViewById(R.id.tvMarqueeTitle);
        btnMedicineReminder = findViewById(R.id.btnMedicineReminder);
        btnSymptomAnalysis = findViewById(R.id.btnSymptomAnalysis);
        btnVitalMonitoring = findViewById(R.id.btnVitalMonitoring);
        btnGenerateReports = findViewById(R.id.btnGenerateReports);

        // Start marquee effect by requesting focus
        tvMarqueeTitle.setSelected(true);

        tvMarqueeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SeasonalActivity.class);
                startActivity(intent);
            }
        });


        // Set click listeners for each block
        btnMedicineReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate or show a Toast
                Intent intent = new Intent(DashboardActivity.this, MedicineReminderActivity.class);
                startActivity(intent);

            }
        });

        btnSymptomAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, SymptomsActivity.class);
                startActivity(intent);
            }
        });

        btnVitalMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, VitalsActivity.class);
                startActivity(intent);
            }
        });

        btnGenerateReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, HealthReportActivity.class);
                startActivity(intent);
            }
        });

        tvLogout = findViewById(R.id.tvLogout);

        // Logout Click Listener
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LoginActivity
                Intent intent = new Intent(DashboardActivity.this, Login.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }
}
