package com.example.survirun.activity.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.activity.account.SignInActivity;
import com.example.survirun.activity.account.SignUpActivity;
import com.example.survirun.databinding.ActivitySplash2Binding;

public class SplashActivity2 extends AppCompatActivity {
    ActivitySplash2Binding binding;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplash2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        dialog = new Dialog(SplashActivity2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        final AnimationDrawable drawableRun =
                (AnimationDrawable) binding.runImageView.getBackground();
        drawableRun.start();

        final AnimationDrawable drawableHands =
                (AnimationDrawable) binding.handsImageView.getBackground();
        drawableHands.start();


        binding.ground.setMinimumWidth(binding.layout.getWidth() * 2);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.run_anim);
        binding.ground.startAnimation(anim);

        binding.signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity2.this, SignInActivity.class);
            startActivity(intent);
        });
        binding.signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity2.this, SignUpActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> finishAffinity());
    }
}