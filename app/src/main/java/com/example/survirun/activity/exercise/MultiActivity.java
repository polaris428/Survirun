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
            /*Intent t = new Intent(MultiActivity.this, MultiMapActivity.class);
            String k = "{\"roomName\":1,\"users\":[{\"email\":\"sadf@sdf.com\",\"username\":\"ttt\",\"latitude\":37.121,\"longitude\":121.213,\"role\":1},{\"email\":\"sad1f@sdf.com\",\"username\":\"12314\",\"latitude\":37.163,\"longitude\":122.1152,\"role\":0},{\"email\":\"iam@2tle.io\",\"username\":\"Dobby\",\"latitude\":37.555,\"longitude\":122.1134,\"role\":0}]}";
            t.putExtra("jsonString",k);*/
            startActivity(t);
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }
}