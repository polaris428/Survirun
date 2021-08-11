package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityUserGoalBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        binding.tvCalorie.setText(String.valueOf(calorie));
        binding.tvTime.setText(h+"h "+m+"m");
        binding.tvKm.setText(String.valueOf(km));

        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, MainActivity.class);
            startActivity(intent);
        });


        binding.btnRetouch.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, UserGoalModifyActivity.class);
            startActivity(intent);
        });
    }
}