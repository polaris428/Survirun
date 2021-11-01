package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.survirun.databinding.ActivityUserSettingBinding;

public class UserSettingActivity extends AppCompatActivity {
    private ActivityUserSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }
}