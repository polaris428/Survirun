package com.example.survirun.Medel;

import com.google.gson.annotations.SerializedName;

public class ScoreModel {
    @SerializedName("calorie")
    public int todayCalorie;
    @SerializedName("time")
    public int todayExerciseTime;
    @SerializedName("km")
    public double todayKm;
}
