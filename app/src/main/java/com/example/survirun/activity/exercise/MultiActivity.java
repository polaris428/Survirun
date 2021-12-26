package com.example.survirun.activity.exercise;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.daimajia.androidanimations.library.flippers.FlipInXAnimator;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMultiBinding;

public class MultiActivity extends AppCompatActivity {
    ActivityMultiBinding binding;
    AnimatorSet front_anim;
    AnimatorSet back_anim;
    Boolean isFront = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        binding.survivorCardView.setCameraDistance(8000*scale);
        binding.cardView.setCardElevation(8000*scale);

        front_anim = (AnimatorSet)AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        back_anim = (AnimatorSet)AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        binding.survivorCardView.setOnLongClickListener(v -> {
            if(isFront){
                front_anim.setTarget(binding.survivorCardView);
                back_anim.setTarget(binding.cardView);
                front_anim.start();
                back_anim.start();
                isFront = false;
            }else{
                front_anim.setTarget(binding.cardView);
                back_anim.setTarget(binding.survivorCardView);
                back_anim.start();
                front_anim.start();
                isFront = true;
            }
            return true;
        });
    }
}