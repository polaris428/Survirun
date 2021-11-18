package com.example.survirun.activity.exercise;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.survirun.BottomSheetModeSelectFragment;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity implements BottomSheetModeSelectFragment.BottomSheetListener {
    ActivityExercisePreparationBinding binding;
    BottomSheetModeSelectFragment selectFragment;
    String title;
    String calorie;
    String km;
    String min;
    String hour;
    int level;
    int zombieCountIntent;
    int time;

    boolean zombieMode = true;
    boolean isCheckLevel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectFragment = new BottomSheetModeSelectFragment();

        Intent exerciseSelection = getIntent();
        title = exerciseSelection.getStringExtra("title");
        calorie = exerciseSelection.getStringExtra("calorie");
        km = exerciseSelection.getStringExtra("km");
        hour = exerciseSelection.getStringExtra("hour");
        min = exerciseSelection.getStringExtra("min");
        level = exerciseSelection.getIntExtra("level", 1);

        int ran = (int) ((Math.random() * 10000) % 10) + 1;
        if (ran == 11) ran = 1;
        binding.proverbTextView.setText(R.string.saying_ + ran);

        binding.exerciseTitleTextview.setText(title);
        binding.calorieTextView.setText(calorie);
        if (hour.equals("0")) {
            binding.hourTextView.setVisibility(View.GONE);
            binding.hourUnitTextView.setVisibility(View.GONE);

        } else {
            binding.hourTextView.setText(hour);
        }
        if (min.equals("0") && hour.equals("0")) {
            binding.minTextView.setText("0");
        } else if (min.equals("0")) {
            binding.minTextView.setVisibility(View.GONE);
            binding.minUnitTextView.setVisibility(View.GONE);

        } else {
            binding.minTextView.setText(min);
        }


        binding.kmTextView.setText(km);

        setAnimation(binding.constraint);

        binding.exerciseStartButton.setOnClickListener(v -> {
            selectFragment.show(getSupportFragmentManager(), "bottomSheet");

        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        time = Integer.parseInt(hour);
        time = time * 60 + Integer.parseInt(min);
        Log.d("time", time + "");
    }

    @Override
    public void onClickStart() {
        if (!isCheckLevel) {
            Toast.makeText(getApplicationContext(), R.string.choose_level, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Integer> modeList = new ArrayList();
            Intent intent = new Intent(ExercisePreparationActivity.this, MapActivity.class);

            intent.putExtra("title", title);
            intent.putExtra("calorie", Integer.parseInt(calorie));
            intent.putExtra("km", Double.parseDouble(km));
            intent.putExtra("time", time);
            intent.putExtra("min", min);
            intent.putExtra("level", level);
            intent.putExtra("zombieCount", zombieCountIntent);
            intent.putExtra("zombieMode", zombieMode);

            Log.d("asdf", String.valueOf(modeList));
            startActivity(intent);
        }
    }

    @Override
    public void onCheckZombie(boolean isCheck) {
        zombieMode = isCheck;
    }

    @Override
    public void onCheckLevel(int zombieCount, boolean isCheckLed) {
        zombieCountIntent = zombieCount;
        isCheckLevel = isCheckLed;
        Log.d("asdf", String.valueOf(zombieCount));
    }


    private void setAnimation(ConstraintLayout constraintLayout) {
        ValueAnimator anim = ValueAnimator.ofInt(1, 1000);
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            constraintLayout.getLayoutParams().height = value.intValue();
            constraintLayout.requestLayout();
        });
        anim.start();
    }

}