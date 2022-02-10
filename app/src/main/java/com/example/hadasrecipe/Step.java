package com.example.hadasrecipe;

public class Step {
    int phase;
    String description;

    public Step(int phase, String description) {
        this.phase = phase;
        this.description = description;
    }

    public Step() {
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
