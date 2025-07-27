package com.example.sympto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class SymptomsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText etSymptoms;
    private TextView tvResponse;
    private Button btnSubmit, btnBack, btnVoiceInput;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        // Initialize UI components
        etSymptoms = findViewById(R.id.etSymptoms);
        tvResponse = findViewById(R.id.tvResponse);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
        btnVoiceInput = findViewById(R.id.btnVoiceInput);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);

        // Check microphone permissions
        checkPermissions();

        // Submit button click listener
        btnSubmit.setOnClickListener(view -> {
            String symptoms = etSymptoms.getText().toString().trim();

            if (symptoms.isEmpty()) {
                Toast.makeText(this, "Please enter symptoms!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress
            tvResponse.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.animate().alpha(1.0f).setDuration(300).start();

            Log.d("SymptomsActivity", "Submitting symptoms: " + symptoms);

            // Call AI analysis (Replace AIHelper with your own implementation)
            AIHelper.getAIAnalysis(symptoms, new AIHelper.AIResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    progressBar.animate().alpha(0.0f).setDuration(300).withEndAction(() -> {
                        progressBar.setVisibility(View.GONE);
                    }).start();

                    // Show only the predicted disease
                    tvResponse.setText("Predicted Disease: " + response);
                    tvResponse.setVisibility(View.VISIBLE);
                    etSymptoms.setText(""); // Clear input after submit

                    // Auto-scroll to bottom of the ScrollView
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                }

                @Override
                public void onError(String error) {
                    progressBar.animate().alpha(0.0f).setDuration(300).withEndAction(() -> {
                        progressBar.setVisibility(View.GONE);
                    }).start();

                    Log.e("API_ERROR", "Full Error: " + error);
                    tvResponse.setText("Error: Unable to predict disease");
                    tvResponse.setVisibility(View.VISIBLE);

                    Toast.makeText(SymptomsActivity.this, "Error: Unable to predict disease", Toast.LENGTH_LONG).show();

                    // Auto-scroll to bottom of the ScrollView
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                }
            });
        });

        // Voice input button click listener
        btnVoiceInput.setOnClickListener(v -> startVoiceInput());

        // Back button click listener
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(SymptomsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not supported on this device!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                etSymptoms.setText(result.get(0));
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }
    }
}
