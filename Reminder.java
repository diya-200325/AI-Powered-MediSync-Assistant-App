package com.example.sympto;

public class Reminder {
    private String medicineName;
    private String frequency;
    private String time;

    public Reminder() {
        // Required for Firebase
    }

    public Reminder(String medicineName, String frequency, String time) {
        this.medicineName = medicineName;
        this.frequency = frequency;
        this.time = time;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getTime() {
        return time;
    }
}
