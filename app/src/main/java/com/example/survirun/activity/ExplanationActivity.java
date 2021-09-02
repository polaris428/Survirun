package com.example.survirun.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.survirun.databinding.ActivityExplanationBinding;

public class ExplanationActivity extends Activity {
    ActivityExplanationBinding binding;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExplanationBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        result = intent.getStringExtra("info");
        binding.informationTextview.setText(result);

    }
}