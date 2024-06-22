package com.example.workingwithcalling;

public class CallLogEntry {
    private String number;
    private String type;
    private String date;
    private String duration;

    public CallLogEntry(String number, String type, String date, String duration) {
        this.number = number;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }
}
