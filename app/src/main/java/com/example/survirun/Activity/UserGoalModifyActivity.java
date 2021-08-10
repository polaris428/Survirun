package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityUserGoalModifyBinding;

public class UserGoalModifyActivity extends AppCompatActivity {
    private ActivityUserGoalModifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}