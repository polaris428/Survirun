package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.survirun.activity.account.SignInActivity;
import com.example.survirun.activity.account.SignUpActivity;
import com.example.survirun.databinding.ActivitySplash2Binding;

public class SplashActivity2 extends AppCompatActivity {
    ActivitySplash2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplash2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.SignInButton.setOnClickListener(v -> {
            Intent intent=new Intent(SplashActivity2.this, SignInActivity.class);
            startActivity(intent);
        });
        binding.signUpButton.setOnClickListener(v -> {
            Intent intent=new Intent(SplashActivity2.this, SignUpActivity.class);
            startActivity(intent);
        });


    }
}