package com.example.sympto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import java.util.Map;

public class HealthReportActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvReportData;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_report);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        tvReportData = findViewById(R.id.tvReportData);
        btnBack = findViewById(R.id.btnBack);

        // Button click to go back
        btnBack.setOnClickListener(v -> finish());

        // Fetch and display the health report
        fetchHealthReport();
    }

    private void fetchHealthReport() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail(); // Get logged-in user's email

        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("Users");

        usersRef.whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        String allergies = documentSnapshot.getString("allergies");
                        String medicalIssues = documentSnapshot.getString("medicalIssues");
                        String medicationDetails = documentSnapshot.getString("medicationDetails");
                        String medicationDuration = documentSnapshot.getString("medicationDuration");

                        Map<String, Object> emergencyContact = (Map<String, Object>) documentSnapshot.get("emergencyContact");
                        String emergencyName = emergencyContact != null ? (String) emergencyContact.get("name") : "N/A";
                        String emergencyPhone = emergencyContact != null ? (String) emergencyContact.get("phone") : "N/A";
                        String emergencyRelation = emergencyContact != null ? (String) emergencyContact.get("relation") : "N/A";

                        String report = "---- Health Report ----\n\n" +
                                "Medical Issues: " + medicalIssues + "\n" +
                                "Allergies: " + allergies + "\n\n" +
                                "Medication:\n" +
                                "Details: " + medicationDetails + "\n" +
                                "Duration: " + medicationDuration + "\n\n" +
                                "Emergency Contact:\n" +
                                "Name: " + emergencyName + "\n" +
                                "Phone: " + emergencyPhone + "\n" +
                                "Relation: " + emergencyRelation + "\n";

                        tvReportData.setText(report);
                    } else {
                        Toast.makeText(this, "No health report found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}