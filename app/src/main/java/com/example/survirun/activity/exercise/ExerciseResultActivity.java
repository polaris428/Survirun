package com.example.survirun.activity.exercise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.Medel.ScoreModel;
import com.example.survirun.Medel.UserModel;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;
import com.example.survirun.databinding.ActivityExerciseResultBinding;
import com.example.survirun.server.ServerClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;

public class ExerciseResultActivity extends AppCompatActivity {
    private ActivityExerciseResultBinding binding;
    ScoreModel scoreModel;


    SharedPreferences sf;

    String token;
    String data;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        data=sf.getString("data","2021-90-0");

        int kcal = getIntent().getIntExtra("kcal", 0);
        double km = getIntent().getDoubleExtra("walkedDistanceToKm", 0);
        int timeToSec = getIntent().getIntExtra("timeToSec", 0);

        binding.endCalorie.setText(String.format("%d", kcal));
        binding.endKm.setText(String.format("%.2f", km));
        int sec = timeToSec % 60;
        int min = timeToSec / 60;
        int hour = timeToSec / 3600;
        binding.endTime.setText(String.format("%d:%d:%d", hour, min, sec));
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
}