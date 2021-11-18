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
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.data.ScoreData;
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
    int myScore;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        data=sf.getString("data","2021-90-0");
        myScore=sf.getInt("score",0);
        Log.d(String.valueOf(myScore),String.valueOf(myScore));
        Intent getIntent=getIntent();

        title=getIntent.getStringExtra("title");
        int kcal = getIntent().getIntExtra("kcal", 0);
        double km = getIntent().getDoubleExtra("walkedDistanceToKm", 0);
        int timeToSec = getIntent().getIntExtra("timeToSec", 0);
        int hp = getIntent().getIntExtra("hp",0);
        setAnimation(binding.constraint);
        binding.calorieTextView.setText(String.format("%d", kcal));
        binding.kmTextView.setText(String.format("%.2f", km));
        binding.exerciseTitleTextview.setText(title);

        int sec = timeToSec % 60;
        int min = timeToSec / 60;
        int hour = timeToSec / 3600;

        int kcalMok = getIntent().getIntExtra("calorie",1);
        int level = getIntent().getIntExtra("level",1);
        double kmMok = getIntent().getDoubleExtra("kmMok",1);
        int timeMok = getIntent().getIntExtra("time",1);
        int nanEdo = getIntent().getIntExtra("zombieCount",5);
        binding.timeTextView.setText(String.format("%d:%d:%d", hour, min, sec));
        binding.goMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreModel scoreModel=new ScoreModel();
                scoreModel.todayKm=km;
                scoreModel.todayExerciseTime=timeToSec;
                scoreModel.todayCalorie=kcal;

                calcScore(token,level,nanEdo,hp,timeToSec,km,kcal,timeMok,kmMok,kcalMok);
                Call<ExerciseData>call= ServerClient.getServerService().patchUploadExercise(token,scoreModel);
                call.enqueue(new Callback<ExerciseData>() {
                    @Override
                    public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                        if(response.isSuccessful()){
                            Call<ExerciseData> call2 = ServerClient.getServerService().getExercise(token);
                            call2.enqueue(new Callback<ExerciseData>() {
                                @Override
                                public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;

                                        SharedPreferences sf;

                                        SharedPreferences.Editor editor;
                                        sf = getSharedPreferences("exercise", MODE_PRIVATE);
                                        editor=sf.edit();
                                        editor.putString("data", response.body().date);
                                        editor.putInt("calorie", response.body().calorie);
                                        editor.putFloat("km", (float) response.body().km);
                                        editor.putInt("time",response.body().time);
                                        editor.commit();
                                        startActivity(new Intent(ExerciseResultActivity.this, MainActivity.class));
                                        finish();

                                    }

                                }


                                @Override
                                public void onFailure(Call<ExerciseData> call, Throwable t) {

                                }
                            });


                        }else {
                            Toast.makeText(ExerciseResultActivity.this, "전송 실패패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExerciseData> call, Throwable t) {

                    }
                });


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

    private void calcScore(String token,int exerciseLevel, int nanEdo, int hp,int timeToSec, double km, int kcal, int timeMok, double kmMok, int kcalMok) {
        int score = 0;
        score += exerciseLevel*100;
        score += hp*10*nanEdo;
        if(timeToSec>= timeMok) score += 500;
        if(km >=kmMok) score += 500;
        if(kcal >=kcalMok) score+=500;
        /*this is bonus*/
        if((timeToSec >= timeMok) && (km >= kmMok) && (kcal >= kcalMok)) {
            score+= 500;
        }
        if(hp==100) score+=100;
        //return score;
        if(myScore<score){
            Call<ScoreData> call2 = ServerClient.getServerService().patchScore(token,score);
            call2.enqueue(new Callback<ScoreData>() {
                @Override
                public void onResponse(Call<ScoreData> call, Response<ScoreData> response) {

                }

                @Override
                public void onFailure(Call<ScoreData> call, Throwable t) {
                    Toast.makeText(ExerciseResultActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        }




    }
}