package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.survirun.BottomSheetModeSelectFragment;
import com.example.survirun.R;
import com.example.survirun.activity.exercise.MapActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity implements BottomSheetModeSelectFragment.BottomSheetListener {
    ActivityExercisePreparationBinding binding;
    BottomSheetModeSelectFragment selectFragment;
    String title;
    String calorie;
    String km;
    String  time;
    int level;
    int zombieCountIntent;

    boolean zombieMode=true;
    boolean isCheckLevel=false;

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
        time = exerciseSelection.getStringExtra("min");
        level= exerciseSelection.getIntExtra("level",1);

        int ran = (int)((Math.random()*10000)%10)+1;
        if(ran==11) ran=1;
        binding.proverbTextView.setText(R.string.saying_+ ran);

        binding.exerciseTitleTextview.setText(title);
        binding.calorieTextView.setText(calorie);
        binding.timeTextView.setText(String.valueOf(time));
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
        if (!isCheckLevel){
            Toast.makeText(getApplicationContext(), "난이도를 선택", Toast.LENGTH_SHORT).show();
        }
        else {
            ArrayList<Integer> modeList = new ArrayList();
            Intent intent = new Intent(ExercisePreparationActivity.this, MapActivity.class);

            intent.putExtra("title", title);
            intent.putExtra("calorie", calorie);
            intent.putExtra("km", km);
            intent.putExtra("time", time);
            intent.putExtra("level", level);
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