package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.data.ExerciseData;

import com.example.survirun.databinding.FragmentUserBinding;
import com.example.survirun.server.ServerClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    int progress = 0;
    int goalCalorie;
    int goalTime;
    double goalKm;

    String token;
    String name;
    SharedPreferences sf;
    SharedPreferences goal;
    SharedPreferences exercise;
    SharedPreferences yesterdayExercise;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        goal = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        goalCalorie = goal.getInt("calorie", 400);
        goalTime = goal.getInt("time", 60);
        goalKm = goal.getInt("km", 5);

        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        name=sf.getString("name","");
        exercise=getContext().getSharedPreferences("exercise",MODE_PRIVATE);
        binding.dateTextview.setText(exercise.getString("data",""));
        score(exercise.getInt("calorie",0),exercise.getInt("time",0),exercise.getFloat("km",0));

        yesterdayExercise= getContext().getSharedPreferences("yesterdayExercise", MODE_PRIVATE);



        return view;
    }

    void score(int calorie,int time ,double km){
        binding.calorieTextview.setText(String.valueOf(calorie));
        binding.timeTextview.setText(String.valueOf(time));
        binding.kmTextview.setText(String.valueOf(km));
        yesterdayExercise= getContext().getSharedPreferences("yesterdayExercise", MODE_PRIVATE);
        binding.eveCalorieTextview.setText(String.valueOf(yesterdayExercise.getInt("calorie",0)-calorie));
        binding.eveKmTextview.setText(String.valueOf(km-yesterdayExercise.getFloat("km",0)));
        binding.eveTimeTextview.setText(String.valueOf(time-yesterdayExercise.getInt("time",0)));

        if (goalCalorie / 2 < calorie) {

            progress = progress + 25;
            binding.calorieCardView.setCardBackgroundColor(Color.YELLOW);
        }
        if (goalTime / 2 < time) {
            progress = progress + 25;
            binding.exerciseTimeCardView.setCardBackgroundColor(Color.YELLOW);
        }
        if (goalKm / 2 < km) {
            progress = progress + 25;
            binding.kmCardView.setCardBackgroundColor(Color.YELLOW);
        }

        if (goalCalorie <= calorie) {
            progress = progress + 8;
            binding.arcProgress.setProgress(progress);
            binding.calorieCardView.setCardBackgroundColor(Color.GREEN);
        }
        if (goalTime <= time) {
            progress = progress + 8;
            binding.arcProgress.setProgress(progress);
            binding.exerciseTimeCardView.setCardBackgroundColor(Color.GREEN);
        }
        if (goalKm <= time) {
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
