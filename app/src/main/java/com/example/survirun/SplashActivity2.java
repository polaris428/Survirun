package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

        final AnimationDrawable drawableRun =
                (AnimationDrawable) binding.runImageView.getBackground();
        drawableRun.start();

        final AnimationDrawable drawableHands =
                (AnimationDrawable) binding.handsImageView.getBackground();
        drawableHands.start();


        binding.ground.setMinimumWidth(binding.layout.getWidth()*2);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.run_anim);
        binding.ground.startAnimation(anim);

        binding.signInButton.setOnClickListener(v -> {
            Intent intent=new Intent(SplashActivity2.this, SignInActivity.class);
            startActivity(intent);
        });
        binding.signUpButton.setOnClickListener(v -> {
            Intent intent=new Intent(SplashActivity2.this, SignUpActivity.class);
            startActivity(intent);
        });


    }
}