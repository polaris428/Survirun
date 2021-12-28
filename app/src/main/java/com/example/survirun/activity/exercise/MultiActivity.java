package com.example.survirun.activity.exercise;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMultiBinding;


public class MultiActivity extends AppCompatActivity {
    ActivityMultiBinding binding;
    Boolean  isFrontSurvivor = true,
            isFrontResearcher = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startButton.setOnClickListener(v -> {
            Intent t = new Intent(MultiActivity.this, QueueActivity.class);
            startActivity(t);
        });

        binding.survivorCardView.setOnLongClickListener(v -> {
            if (isFrontSurvivor) {
                binding.survivorFront.setVisibility(View.INVISIBLE);
                binding.survivorBack.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.survivorBack);
                isFrontSurvivor = false;
            } else {
                binding.survivorFront.setVisibility(View.VISIBLE);
                binding.survivorBack.setVisibility(View.INVISIBLE);

                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.survivorFront);
                isFrontSurvivor = true;
            }
            return true;
        });
        binding.researcherCardView.setOnLongClickListener(v -> {
            if (isFrontResearcher) {
                binding.researcherFront.setVisibility(View.INVISIBLE);
                binding.researcherBack.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.researcherBack);
                isFrontResearcher = false;
            } else {
                binding.researcherFront.setVisibility(View.VISIBLE);
                binding.researcherBack.setVisibility(View.INVISIBLE);

                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.researcherFront);
                isFrontResearcher = true;
            }
            return true;
        });

    }
}