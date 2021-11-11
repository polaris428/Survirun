package com.example.survirun.activity.user;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityUserGoalBinding;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserGoalActivity extends AppCompatActivity {
    private ActivityUserGoalBinding binding;
    int calorie;
    int time;
    int km;
    int yesterdayCalorie;
    int yesterdayTime;
    int yesterdayKm;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("goal", MODE_PRIVATE);
        calorie = sf.getInt("calorie", 400);
        time = sf.getInt("time", 60);
        km = sf.getInt("km", 5);

        SharedPreferences yesterdaySf = getSharedPreferences("yesterday", MODE_PRIVATE);
        yesterdayCalorie=yesterdaySf.getInt("calorie",0);
        yesterdayTime=yesterdaySf.getInt("time",0);
        yesterdayKm=yesterdaySf.getInt("yesterdayKm",0);
        binding.yesterdayTextView.setText(yesterdayCalorie+"칼로리"+yesterdayTime+"시간"+yesterdayKm+"Km");

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        String formatedNow = now.format(formatter);

        binding.dataTextview.setText(formatedNow+"");


        String h = String.valueOf(time / 60);
        String m = String.valueOf(time % 60);
        binding.calorieTextview.setText(String.valueOf(calorie));
        binding.timeTextview.setText(h + "h " + m + "m");
        binding.kmTextview.setText(String.valueOf(km));
        binding.expandImageButton.setOnClickListener(v -> {
            if(binding.yesterdayTextView.getVisibility()== View.GONE){
                binding.expandImageButton.setImageResource(R.drawable.ic_upblack);
                binding.yesterdayTextView.setVisibility(View.VISIBLE);

            }else {
                binding.expandImageButton.setImageResource(R.drawable.ic_downblack);
                binding.yesterdayTextView.setVisibility(View.GONE);
            }
        });
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