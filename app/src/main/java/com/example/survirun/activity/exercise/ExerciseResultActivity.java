package com.example.survirun.activity.exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.Medel.ScoreModel;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.databinding.ActivityExerciseResultBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseResultActivity extends AppCompatActivity {
    private ActivityExerciseResultBinding binding;
    ScoreModel scoreModel;


    SharedPreferences sf;

    String token;
    String data;
    String title;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        data=sf.getString("data","2021-90-0");
        Intent getIntent=getIntent();

        title=getIntent.getStringExtra("title");
        int kcal = getIntent().getIntExtra("kcal", 0);
        double km = getIntent().getDoubleExtra("walkedDistanceToKm", 0);
        int timeToSec = getIntent().getIntExtra("timeToSec", 0);

        setAnimation(binding.constraint);
        binding.calorieTextView.setText(String.format("%d", kcal));
        binding.kmTextView.setText(String.format("%.2f", km));
        binding.exerciseTitleTextview.setText(title);

        int sec = timeToSec % 60;
        int min = timeToSec / 60;
        int hour = timeToSec / 3600;
        binding.timeTextView.setText(String.format("%d:%d:%d", hour, min, sec));
        binding.goMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreModel scoreModel=new ScoreModel();
                scoreModel.todayKm=km;
                scoreModel.todayExerciseTime=timeToSec;
                scoreModel.todayCalorie=kcal;
                Call<ExerciseData>call= ServerClient.getServerService().patchUploadExercise(token,scoreModel);
                call.enqueue(new Callback<ExerciseData>() {
                    @Override
                    public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                        if(response.isSuccessful()){

                        }else {
                            Toast.makeText(ExerciseResultActivity.this, "전송 실패패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExerciseData> call, Throwable t) {

                    }
                });

                startActivity(new Intent(ExerciseResultActivity.this, MainActivity.class));
            }
        });

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