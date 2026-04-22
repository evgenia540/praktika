package com.example.praktika;

public class Project {
    private String name;
    private String duration;
    private String status;

    public Project(String name, String duration, String status) {
        this.name = name;
        this.duration = duration;
        this.status = status;
    }

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getStatus() { return status; }
}