package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.survirun.BottomSheetModeSelectFragment;
import com.example.survirun.activity.exercise.MapActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity implements BottomSheetModeSelectFragment.BottomSheetListener {
    ActivityExercisePreparationBinding binding;
    boolean isCheckedZombie = false;
    BottomSheetModeSelectFragment selectFragment;
    String title;
    String calorie;
    String km;
    String time;

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
        time = exerciseSelection.getStringExtra("time");

        binding.exerciseTitleTextview.setText(title);
        binding.calorieTextView.setText(calorie);
        binding.timeTextView.setText(time);
        binding.kmTextView.setText(km);

        setAnimation(binding.constraint);

        binding.exerciseStartButton.setOnClickListener(v -> {
            selectFragment.show(getSupportFragmentManager(), "bottomSheet");
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onClickStart() {
        ArrayList<Integer> modeList = new ArrayList();
        if (isCheckedZombie) modeList.add(MapActivity.ZOMBIE_MODE);
        else modeList.add(MapActivity.DEFAULT_MODE);
        Intent i = new Intent(ExercisePreparationActivity.this, MapActivity.class);
        i.putExtra("mode", modeList);
        Log.d("asdf", String.valueOf(modeList));
        startActivity(i);
    }

    @Override
    public void onCheckZombie(boolean isCheck) {
        isCheckedZombie = isCheck;
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