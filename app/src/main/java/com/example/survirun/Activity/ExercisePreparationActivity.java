package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.example.survirun.ExplanationActivity;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;
import com.example.survirun.databinding.ActivityLoginBinding;

public class ExercisePreparationActivity extends AppCompatActivity {
    ActivityExercisePreparationBinding binding;
    boolean isCheckedZombie = false,
            isCheckedStory = false,
            isCheckedGPS = false,
            isCheckedNo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = new Intent(ExercisePreparationActivity.this, ExplanationActivity.class);

        binding.infoZombieButton.setOnClickListener(v -> {
            intent.putExtra("info", "zombie");
            startActivity(intent);
        });

        binding.infoStoryButton.setOnClickListener(v -> {
            intent.putExtra("info", "story");
            startActivity(intent);
        });

        binding.infoGpsButton.setOnClickListener(v -> {
            intent.putExtra("info", "gps");
            startActivity(intent);
        });
        binding.infoNoButton.setOnClickListener(v -> {
            intent.putExtra("info", "no");
            startActivity(intent);
        });
        binding.zombieSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckedZombie = isChecked;

        });

        binding.exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExercisePreparationActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

    }
}