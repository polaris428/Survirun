package com.example.survirun.activity.user;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityUserGoalBinding;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserGoalActivity extends AppCompatActivity {
    private ActivityUserGoalBinding binding;
    int calorie;
    int time;
    int km;
    int yesterdayCalorie;
    int yesterdayTime;
    int yesterdayKm;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("goal", MODE_PRIVATE);
        calorie = sf.getInt("calorie", 400);
        time = sf.getInt("time", 60);
        km = sf.getInt("km", 5);

        SharedPreferences yesterdaySf = getSharedPreferences("yesterday", MODE_PRIVATE);
        yesterdayCalorie = yesterdaySf.getInt("calorie", 0);
        yesterdayTime = yesterdaySf.getInt("time", 0);
        yesterdayKm = yesterdaySf.getInt("yesterdayKm", 0);
        binding.yesterdayTextView.setText(yesterdayCalorie + "kcal " + yesterdayTime + getString(R.string.hour) + " " + yesterdayKm + "Km");

        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        String formatedNow = now.format(formatter);
        int ran = (int) ((Math.random() * 10000) % 10) + 1;
        if (ran == 11) ran = 1;
        binding.proverbTextView.setText(R.string.saying_ + ran);
        binding.dataTextview.setText(formatedNow + "");


        String h = String.valueOf(time / 60);
        String m = String.valueOf(time % 60);
        binding.calorieTextview.setText(String.valueOf(calorie) + "kcal");
        if ((time / 60 != 0 && time % 60 != 0) || time == 0) {
            binding.timeTextview.setText(h + getString(R.string.hour) + " " + m + getString(R.string.min));
        } else if (time / 60 == 0) {
            binding.timeTextview.setText(m + getString(R.string.min));
        } else if (time % 60 == 0) {
            binding.timeTextview.setText(h + getString(R.string.hour));
        }
        binding.kmTextview.setText(String.valueOf(km) + "Km");

        Handler handler = new Handler();
        binding.expandImageButton.setOnClickListener(v -> {
            if (binding.yesterdayTextView.getVisibility() == View.GONE) {
                ValueAnimator anim = ValueAnimator.ofInt(binding.constraintLayout.getHeight(), binding.constraintLayout.getMaxHeight());
                setAnimation(anim, binding.constraintLayout);
                binding.expandImageButton.setImageResource(R.drawable.ic_upblack);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.yesterdayTextView.setVisibility(View.VISIBLE);
                    }
                }, 500);

            } else {
                binding.expandImageButton.setImageResource(R.drawable.ic_downblack);
                ValueAnimator anim = ValueAnimator.ofInt(binding.constraintLayout.getMaxHeight(), binding.constraintLayout.getMinHeight());
                binding.yesterdayTextView.setVisibility(View.GONE);
                setAnimation(anim, binding.constraintLayout);
            }
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });

        binding.retouchButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalActivity.this, UserGoalModifyActivity.class);
            startActivity(intent);
        });
    }

    private void setAnimation(ValueAnimator anim, ConstraintLayout constraintLayout) {
        anim.setDuration(500);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            constraintLayout.getLayoutParams().height = value.intValue();
            constraintLayout.requestLayout();
        });
        anim.start();
    }
}