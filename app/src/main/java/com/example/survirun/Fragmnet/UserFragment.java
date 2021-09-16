package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.UserPageActivity;
import com.example.survirun.activity.UserGoalActivity;
import com.example.survirun.Medel.UserModel;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.databinding.FragmentUserBinding;
import com.example.survirun.server.ServerClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    String uid = FirebaseAuth.getInstance().getUid();
    Date currentTime = Calendar.getInstance().getTime();

    int progress = 0;
    String token;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences goal = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        int goalCalorie = goal.getInt("calorie", 400);
        int goalTime = goal.getInt("time", 60);
        int goalkm = goal.getInt("km", 5);
        SharedPreferences sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        token=sf.getString("token","");
        String date = new SimpleDateFormat("(yy.MM.dd)", Locale.getDefault()).format(currentTime);
        binding.dateTextview.setText(date);


        binding.userPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserPageActivity.class);
            startActivity(intent);
        });
        Call<ExerciseData>call= ServerClient.getServerService().getExercise(token);
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if(response.isSuccessful()){
                    binding.kmTextview.setText(response.body().km+"");
                    binding.calorieTextview.setText(response.body().calorie+"");
                    binding.timeTextview.setText(response.body().time+"");
                    if (goalCalorie / 2 < response.body().calorie) {
                        progress = progress + 25;
                        binding.calorieCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalTime / 2 < response.body().time) {
                        progress = progress + 25;
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalkm / 2 < response.body().km) {
                        progress = progress + 25;
                        binding.kmCardView.setCardBackgroundColor(Color.YELLOW);
                    }

                    if (goalCalorie <= response.body().calorie) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.calorieCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalTime <=  response.body().time) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalkm <=response.body().time) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.kmCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (progress >= 99) {
                        progress = 100;
                        binding.arcProgress.setProgress(progress);
                    }


                }
            }

            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {

            }
        });

        binding.goalButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserGoalActivity.class);
            startActivity(intent);
        });

        return view;
    }

}