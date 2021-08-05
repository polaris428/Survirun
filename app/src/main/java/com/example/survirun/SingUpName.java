package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;


import com.example.survirun.databinding.ActivitySingUpNameBinding;

import java.util.Arrays;
import java.util.List;

public class SingUpName extends AppCompatActivity {
    ActivitySingUpNameBinding binding;
    String story;

    int i;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        story= (String) getText(R.string.story_name);
        Handler animationCompleteCallBack = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.i("Log", "Animation Completed");
                return false;
            }
        });

        Typewriter typewriter = new Typewriter(this);
        typewriter.setCharacterDelay(100);
        typewriter.setTextSize(20);
        typewriter.setTextColor(R.color.black);
        typewriter.setTypeface(null, Typeface.NORMAL);
        typewriter.setPadding(20, 20, 20, 20);
        typewriter.setAnimationCompleteListener(animationCompleteCallBack);
        typewriter.animateText(story);
        setContentView(typewriter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(view);

            }
        }, 6000); //딜레이 타임 조절


    }


}