package com.example.sympto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VitalsActivity extends AppCompatActivity {

    // UI components
    EditText etBloodPressure, etHeartRate, etBodyTemp, etRespiratoryRate;
    Button btnAnalyze, btnBack;
    TextView tvAIResponse;

    ProgressDialog progressDialog;

    // ✅ Replace with your Gemini API Key
    private final String API_KEY = "AIzaSyCamePVLHBVMfmgDbNcdubAWATjKoef40M";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        // Bind views
        etBloodPressure = findViewById(R.id.etBloodPressure);
        etHeartRate = findViewById(R.id.etHeartRate);
        etBodyTemp = findViewById(R.id.etBodyTemp);
        etRespiratoryRate = findViewById(R.id.etRespiratoryRate);

        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnBack = findViewById(R.id.btnBack);
        tvAIResponse = findViewById(R.id.tvAIResponse);

        // Analyze button click
        btnAnalyze.setOnClickListener(view -> analyzeVitals());

        // Back button click
        btnBack.setOnClickListener(view -> finish()); // This will go back to the previous screen
    }

    private void analyzeVitals() {
        String bloodPressure = etBloodPressure.getText().toString().trim();
        String heartRate = etHeartRate.getText().toString().trim();
        String bodyTemp = etBodyTemp.getText().toString().trim();
        String respiratoryRate = etRespiratoryRate.getText().toString().trim();

        // Validate inputs
        if (bloodPressure.isEmpty() || heartRate.isEmpty() || bodyTemp.isEmpty() || respiratoryRate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create AI prompt
        String userVitals = "Analyze the following patient vital signs and provide:\n" +
                "1. A prediction of possible future diseases or health risks.\n" +
                "2. Personalized lifestyle recommendations to prevent or manage these potential health issues.\n\n" +
                "Vital Signs:\n" +
                "- Blood Pressure: " + bloodPressure + "\n" +
                "- Heart Rate: " + heartRate + "\n" +
                "- Body Temperature: " + bodyTemp + "\n" +
                "- Respiratory Rate: " + respiratoryRate;

        // Send to Gemini AI
        sendToAI(userVitals);
    }

    private void sendToAI(String prompt) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Analyzing vitals...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // Prepare JSON request for Gemini API
                JSONObject textPart = new JSONObject();
                textPart.put("text", prompt);

                JSONArray partsArray = new JSONArray();
                partsArray.put(textPart);

                JSONObject userContent = new JSONObject();
                userContent.put("role", "user");
                userContent.put("parts", partsArray);

                JSONArray contentsArray = new JSONArray();
                contentsArray.put(userContent);

                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("contents", contentsArray);

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(mediaType, requestBodyJson.toString());

                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=AIzaSyCamePVLHBVMfmgDbNcdubAWATjKoef40M" ;

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseData = response.body().string();

                Log.d("AIResponse", responseData);

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        String aiResponse = candidates.getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        tvAIResponse.setText(aiResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvAIResponse.setText("Failed to parse AI response.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    tvAIResponse.setText("An error occurred: " + e.getMessage());
                });
            }
        }).start();
    }
}
