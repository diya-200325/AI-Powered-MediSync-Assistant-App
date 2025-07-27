package com.example.sympto;

import java.util.List;

public class SeasonalDisease {
    private String name;
    private List<String> precautions;

    public SeasonalDisease() {
        // Empty constructor required for Firestore
    }

    public SeasonalDisease(String name, List<String> precautions) {
        this.name = name;
        this.precautions = precautions;
    }

    public String getName() {
        return name;
    }

    public List<String> getPrecautions() {
        return precautions;
    }
}