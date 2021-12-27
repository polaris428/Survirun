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

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMultiBinding;

public class MultiActivity extends AppCompatActivity {
    ActivityMultiBinding binding;
    AnimatorSet front_anim;
    AnimatorSet back_anim;
    Boolean isFrontSurvivor = true,
            isFrontResearcher = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        binding.survivorCardView.setCameraDistance(8000*scale);
        binding.survivorBackCardView.setCardElevation(8000*scale);
        binding.researcherCardView.setCameraDistance(8000*scale);
        binding.researcherBackCardView.setCardElevation(8000*scale);

        front_anim = (AnimatorSet)AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        back_anim = (AnimatorSet)AnimatorInflater.loadAnimator(this, R.animator.flip_in);
        binding.startButton.setOnClickListener(v -> {
            Intent t = new Intent(MultiActivity.this, QueueActivity.class);
            startActivity(t);
        });
        binding.survivorCardView.setOnLongClickListener(v -> {
            if(isFrontSurvivor){
                front_anim.setTarget(binding.survivorCardView);
                back_anim.setTarget(binding.survivorBackCardView);
                front_anim.start();
                back_anim.start();
                isFrontSurvivor = false;
            }else{
                front_anim.setTarget(binding.survivorBackCardView);
                back_anim.setTarget(binding.survivorCardView);
                back_anim.start();
                front_anim.start();
                isFrontSurvivor = true;
            }
            return true;
        });
        binding.researcherCardView.setOnLongClickListener(v -> {
            if(isFrontResearcher){
                front_anim.setTarget(binding.researcherCardView);
                back_anim.setTarget(binding.researcherBackCardView);
                front_anim.start();
                back_anim.start();
                isFrontResearcher = false;
            }else{
                front_anim.setTarget(binding.researcherBackCardView);
                back_anim.setTarget(binding.researcherCardView);
                back_anim.start();
                front_anim.start();
                isFrontResearcher = true;
            }
            return true;
        });

    }
}