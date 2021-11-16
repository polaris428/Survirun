package com.example.survirun.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityUserGoalModifyBinding;

import java.util.ArrayList;
import java.util.List;

public class UserGoalModifyActivity extends AppCompatActivity {
    private ActivityUserGoalModifyBinding binding;
    boolean isCalorie = true;
    boolean isTimeH = true;
    boolean isTimeM = true;
    boolean isKm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("goal", MODE_PRIVATE);
        int calorie = sf.getInt("calorie", 400);
        int time = sf.getInt("time", 60);
        int km = sf.getInt("km", 5);
        List<String> numList = new ArrayList();


        binding.calorieNumberPicker.setValue(calorie);
        binding.hourNumberPicker.setValue(time / 60);
        binding.minuteNumberPicker.setValue(time % 60);
        binding.kmNumberPicker.setValue(km);

        binding.calorieNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));
        binding.hourNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));
        binding.minuteNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));

        binding.calorieNumberPicker.setOnClickListener(v -> {
            binding.calorieNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.gray));
            binding.calorieNumberPicker.setDividerColor(ContextCompat.getColor(this, R.color.red));
        });

        binding.hourNumberPicker.setOnClickListener(v -> {
            binding.hourNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.gray));
            binding.hourNumberPicker.setDividerColor(ContextCompat.getColor(this, R.color.red));
        });

        binding.minuteNumberPicker.setOnClickListener(v -> {
            binding.minuteNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.gray));
            binding.minuteNumberPicker.setDividerColor(ContextCompat.getColor(this, R.color.red));
        });


        binding.saveButton.setOnClickListener(v -> {
            int inputCalorie = binding.calorieNumberPicker.getValue();
            int inputTime = binding.hourNumberPicker.getValue() * 60 + binding.minuteNumberPicker.getValue();
            int inputKm = binding.kmNumberPicker.getValue();
            SharedPreferences.Editor editor = sf.edit();
            editor.putInt("calorie", inputCalorie);
            editor.putInt("time", inputTime);
            editor.putInt("km", inputKm);
            editor.apply();
            editor.commit();
            finish();
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });

    }
}