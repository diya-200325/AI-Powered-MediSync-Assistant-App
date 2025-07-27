package com.example.sympto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuestionnaireActivity extends AppCompatActivity {

    // UI Components
    private CheckBox cbDiabetes, cbHypertension, cbAsthma, cbHeartDisease, cbNone;
    private RadioGroup rgMedications, rgAllergies;
    private EditText etMedicationDetails, etMedicationDuration, etAllergyDetails;
    private EditText etContactName, etContactPhone, etContactRelation;
    private Button btnSubmit;
    private ProgressBar progressBar;

    // Firestore reference
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_activity);

        // Initialize UI elements and Firestore instance
        initializeUI();
        firestore = FirebaseFirestore.getInstance();

        // Handle medication input visibility
        rgMedications.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMedicationYes) {
                etMedicationDetails.setVisibility(View.VISIBLE);
                etMedicationDuration.setVisibility(View.VISIBLE);
            } else {
                etMedicationDetails.setVisibility(View.GONE);
                etMedicationDuration.setVisibility(View.GONE);
            }
        });

        // Handle allergy input visibility
        rgAllergies.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAllergyYes) {
                etAllergyDetails.setVisibility(View.VISIBLE);
            } else {
                etAllergyDetails.setVisibility(View.GONE);
            }
        });

        // Submit button listener
        btnSubmit.setOnClickListener(v -> {
            Log.d("QuestionnaireActivity", "Submit button clicked");
            progressBar.setVisibility(View.VISIBLE);
            saveDataToFirestore();
        });
    }

    private void initializeUI() {
        // Initialize checkboxes
        cbDiabetes = findViewById(R.id.cbDiabetes);
        cbHypertension = findViewById(R.id.cbHypertension);
        cbAsthma = findViewById(R.id.cbAsthma);
        cbHeartDisease = findViewById(R.id.cbHeartDisease);
        cbNone = findViewById(R.id.cbNone);

        // Initialize radio groups
        rgMedications = findViewById(R.id.rgMedications);
        rgAllergies = findViewById(R.id.rgAllergies);

        // Initialize edit texts
        etMedicationDetails = findViewById(R.id.etMedicationDetails);
        etMedicationDuration = findViewById(R.id.etMedicationDuration);
        etAllergyDetails = findViewById(R.id.etAllergyDetails);

        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        etContactRelation = findViewById(R.id.etContactRelation);

        // Initialize buttons and progress bar
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        // Hide additional input fields initially
        etMedicationDetails.setVisibility(View.GONE);
        etMedicationDuration.setVisibility(View.GONE);
        etAllergyDetails.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void saveDataToFirestore() {
        // Collect medical issues from checkboxes
        StringBuilder issues = new StringBuilder();
        if (cbDiabetes.isChecked()) issues.append("Diabetes, ");
        if (cbHypertension.isChecked()) issues.append("Hypertension, ");
        if (cbAsthma.isChecked()) issues.append("Asthma, ");
        if (cbHeartDisease.isChecked()) issues.append("Heart Disease, ");
        if (cbNone.isChecked() || issues.length() == 0) issues.append("None");

        // Collect medication details
        String medicationAnswer = (rgMedications.getCheckedRadioButtonId() == R.id.rbMedicationYes)
                ? etMedicationDetails.getText().toString().trim()
                : "No medications";

        String medicationDuration = (rgMedications.getCheckedRadioButtonId() == R.id.rbMedicationYes)
                ? etMedicationDuration.getText().toString().trim()
                : "N/A";

        // Collect allergy details
        String allergyAnswer = (rgAllergies.getCheckedRadioButtonId() == R.id.rbAllergyYes)
                ? etAllergyDetails.getText().toString().trim()
                : "No allergies";

        // Collect emergency contact details
        String contactName = etContactName.getText().toString().trim();
        String contactPhone = etContactPhone.getText().toString().trim();
        String contactRelation = etContactRelation.getText().toString().trim();

        // Validate emergency contact info
        if (contactName.isEmpty() || contactPhone.isEmpty() || contactRelation.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill all emergency contact details!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("medicalIssues", issues.toString());
        userData.put("medicationDetails", medicationAnswer);
        userData.put("medicationDuration", medicationDuration);
        userData.put("allergies", allergyAnswer);

        Map<String, String> contactData = new HashMap<>();
        contactData.put("name", contactName);
        contactData.put("phone", contactPhone);
        contactData.put("relation", contactRelation);

        userData.put("emergencyContact", contactData);

        // ✅ Firestore: Update data in the existing user document by UID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("Users")
                .document(uid)
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Data submitted successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to DashboardActivity
                    Intent intent = new Intent(QuestionnaireActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("FirestoreError", "Failed to update data", e);
                    Toast.makeText(this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
