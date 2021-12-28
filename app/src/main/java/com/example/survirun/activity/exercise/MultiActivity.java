package com.example.survirun.activity.exercise;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.survirun.databinding.ActivityMultiBinding;


public class MultiActivity extends AppCompatActivity {
    ActivityMultiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startButton.setOnClickListener(v -> {
            Intent t = new Intent(MultiActivity.this, QueueActivity.class);
            startActivity(t);
        });

    }
}