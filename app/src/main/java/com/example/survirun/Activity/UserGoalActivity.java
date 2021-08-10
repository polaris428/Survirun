package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityUserGoalBinding;

public class UserGoalActivity extends AppCompatActivity {
    private ActivityUserGoalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRetouch.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, UserGoalModifyActivity.class);
            startActivity(intent);
        });
    }
}