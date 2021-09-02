package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.survirun.databinding.ActivityUserGoalBinding;

public class UserGoalActivity extends AppCompatActivity {
    private ActivityUserGoalBinding binding;
    int calorie, time, km;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("goal", MODE_PRIVATE);
        calorie = sf.getInt("calorie", 400);
        time = sf.getInt("time", 60);
        km = sf.getInt("km", 5);

        String h = String.valueOf(time/60);
        String m = String.valueOf(time%60);
        binding.calorieTextview.setText(String.valueOf(calorie));
        binding.timeTextview.setText(h+"h "+m+"m");
        binding.kmTextview.setText(String.valueOf(km));

        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, MainActivity.class);
            startActivity(intent);
        });


        binding.retouchButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, UserGoalModifyActivity.class);
            startActivity(intent);
        });
    }
}