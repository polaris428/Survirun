package com.example.survirun.activity.exercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.survirun.Medel.UserModel;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;
import com.example.survirun.databinding.ActivityExerciseResultBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExerciseResultActivity extends AppCompatActivity {
    private ActivityExerciseResultBinding binding;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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