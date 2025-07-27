package com.example.sympto;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SeasonalActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listView;
    private List<SeasonalDisease> diseaseList;
    private SeasonalDiseasesAdapter adapter;
    private TextView marqueeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasonal);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Setup Toolbar with Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        listView = findViewById(R.id.listView);
        marqueeTextView = findViewById(R.id.marqueeTextView);

        diseaseList = new ArrayList<>();
        adapter = new SeasonalDiseasesAdapter(this, diseaseList);
        listView.setAdapter(adapter);

        // Start marquee effect
        marqueeTextView.setSelected(true);

        // Fetch data from Firestore
        fetchDiseasesFromFirestore();
    }

    private void fetchDiseasesFromFirestore() {
        CollectionReference diseasesRef = db.collection("seasonaldisease");

        diseasesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    diseaseList.clear();
                    List<String> diseaseNames = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        List<String> precautions = (List<String>) document.get("precautions");

                        Log.d("Firestore", "Disease: " + name + ", Precautions: " + precautions);

                        SeasonalDisease disease = new SeasonalDisease(name, precautions);
                        diseaseList.add(disease);
                        diseaseNames.add(name); // Collect names for marquee
                    }

                    adapter.notifyDataSetChanged();

                    // Set the marquee text with disease names
                    setMarqueeText(diseaseNames);

                } else {
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                    Toast.makeText(SeasonalActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setMarqueeText(List<String> diseaseNames) {
        if (diseaseNames.isEmpty()) {
            marqueeTextView.setText("No seasonal diseases found.");
        } else {
            String marqueeText = "Seasonal Diseases: " + TextUtils.join(" | ", diseaseNames);
            marqueeTextView.setText(marqueeText);
        }

        // Start marquee effect again (just to be safe)
        marqueeTextView.setSelected(true);
    }

    // Handle back button in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}