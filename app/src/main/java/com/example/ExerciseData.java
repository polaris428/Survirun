package com.example;

public class ExerciseData {
    String exName;



    int km;
    int hour;
    int calorie;


    public String getExName(int exName) {
        return this.exName;
    }

    public void setExName(String exName) {
        this.exName = exName;
    }
    public int getKm(int km) {
        return this.km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public int getHour(int position) {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getCalorie(int position) {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

}
