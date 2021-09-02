package com.example.survirun.activity.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityExerciseResultBinding;

public class ExerciseResultActivity extends AppCompatActivity {
    private ActivityExerciseResultBinding binding;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_result);

        int kcal = getIntent().getIntExtra("kcal",0);
        double km = getIntent().getDoubleExtra("walkedDistanceToKm",0);
        int timeToSec = getIntent().getIntExtra("timeToSec",0);

        binding.endCalorie.setText(String.format("%d", kcal));
        binding.endKm.setText(String.format("%.2f",km));
        int sec = timeToSec % 60;
        int min = timeToSec / 60;
        int hour = timeToSec / 3600;
        binding.endTime.setText(String.format("%d:%d:%d",hour,min,sec));
        binding.goMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExerciseResultActivity.this, MainActivity.class));
            }
        });
    }
}