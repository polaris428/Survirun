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

        List<String> calorieList = new ArrayList<>();
        for (int i = 100; i < 5001; i += 100) {
            calorieList.add(String.valueOf(i));
        }

        List<String> minList = new ArrayList<>();
        for (int j = 0; j < 56; j += 5) {
            minList.add(String.valueOf(j));
        }


        binding.calorieNumberPicker.setValue(calorie + 100);
        binding.hourNumberPicker.setValue(time / 60);
        binding.minuteNumberPicker.setValue(time % 60 / 5 + 1);
        binding.kmNumberPicker.setValue(km);

        binding.calorieNumberPicker.setMinValue(1);
        binding.calorieNumberPicker.setMaxValue(calorieList.size());
        binding.calorieNumberPicker.setDisplayedValues(calorieList.toArray(new String[calorieList.size()]));

        binding.minuteNumberPicker.setMinValue(1);
        binding.minuteNumberPicker.setMaxValue(minList.size());
        binding.minuteNumberPicker.setDisplayedValues(minList.toArray(new String[minList.size()]));

        binding.calorieNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));
        binding.hourNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));
        binding.minuteNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));
        binding.kmNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.transparent));

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

        binding.kmNumberPicker.setOnClickListener(v -> {
            binding.kmNumberPicker.setTextColor(ContextCompat.getColor(this, R.color.gray));
            binding.kmNumberPicker.setDividerColor(ContextCompat.getColor(this, R.color.red));
        });


        binding.saveButton.setOnClickListener(v -> {
            int inputCalorie = binding.calorieNumberPicker.getValue() * 100;
            int inputTime = binding.hourNumberPicker.getValue() * 60 + (binding.minuteNumberPicker.getValue() - 1) * 5;
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