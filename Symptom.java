package com.example.sympto;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Symptom extends AppCompatActivity {

    EditText symptomInput;
    Button analyzeButton;
    TextView tvResponse;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);   // XML file name

        // Match IDs exactly to your XML
        symptomInput = findViewById(R.id.etSymptoms);      // EditText XML ID
        analyzeButton = findViewById(R.id.btnSubmit);      // Button XML ID
        tvResponse = findViewById(R.id.tvResponse);        // TextView XML ID
        progressBar = findViewById(R.id.progressBar);      // ProgressBar XML ID

        Toast.makeText(this, "Activity loaded!", Toast.LENGTH_SHORT).show();

        analyzeButton.setOnClickListener(v -> processSymptoms());
    }

    private void processSymptoms() {
        String symptoms = symptomInput.getText().toString().trim();

        if (symptoms.isEmpty()) {
            Toast.makeText(this, "Please enter symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvResponse.setVisibility(View.GONE);

        Toast.makeText(this, "Sending symptoms to AI...", Toast.LENGTH_SHORT).show();

        // Fake delay instead of AIHelper
        symptomInput.postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            tvResponse.setVisibility(View.VISIBLE);

            String fakeResponse = "You might have a common cold. Please consult a doctor.";
            tvResponse.setText("AI Response: " + fakeResponse);

        }, 2000);  // Simulate 2-second delay
    }
}
