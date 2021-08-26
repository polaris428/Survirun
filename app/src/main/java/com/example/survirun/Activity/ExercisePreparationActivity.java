package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;
import com.example.survirun.databinding.ActivityLoginBinding;

public class ExercisePreparationActivity extends AppCompatActivity {
    ActivityExercisePreparationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.exerciseStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ExercisePreparationActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });

    }
}