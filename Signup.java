package com.example.sympto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignup;
    private TextView tvLogin;

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ProgressDialog progressDialog; // ✅ For better UX

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnSignup = findViewById(R.id.signup_btn);
        tvLogin = findViewById(R.id.login_text);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnSignup.setOnClickListener(view -> createUser());

        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        });
    }

    private void createUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ✅ Simple validations
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        // ✅ Firebase signup
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        // ✅ Add user data to Firestore after signup
                        addUserToFirestore();

                        // Go to Questionnaire activity
                        startActivity(new Intent(Signup.this, QuestionnaireActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ✅ Save user info to Firestore (email + empty/default fields)
    private void addUserToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String email = currentUser.getEmail(); // ✅ Always get from FirebaseAuth

            // ✅ Pre-fill with empty/default questionnaire answers
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email); // ✅ Auto get email
            userData.put("allergies", ""); // Leave blank, will be updated after questionnaire
            userData.put("medicalIssues", "");
            userData.put("medicationDetails", "");
            userData.put("medicationDuration", "");

            // Optional: Default emergency contact info (can be updated later)
            Map<String, Object> emergencyContact = new HashMap<>();
            emergencyContact.put("name", "");
            emergencyContact.put("phone", "");
            emergencyContact.put("relation", "");

            userData.put("emergencyContact", emergencyContact);

            // ✅ Store in Firestore under "Users" collection with UID as document ID
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Signup.this, "User profile created!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Signup.this, "Failed to create Firestore profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(Signup.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }
}
