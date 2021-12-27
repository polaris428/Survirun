package com.example.survirun.fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.CharacterChangeActivity;
import com.example.survirun.R;

import com.example.survirun.databinding.FragmentUserBinding;


import java.util.Locale;

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

        binding.changeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CharacterChangeActivity.class);
            startActivity(intent);
        });

        return view;
    }

    void score(int calorie,int time ,double km){
        binding.calorieTextview.setText(String.valueOf(calorie));

        int min = time / 60;
        int hour = time / 3600;
        if (hour >= 1) {

            binding.timeHTextview.setText(String.valueOf(hour));
        }else{
            binding.timeHTextview.setVisibility(View.GONE);
            binding.timeHUnitTextview.setVisibility(View.GONE);
            binding.timeMTextview.setText(String.valueOf(min));
        }


        binding.kmTextview.setText(String.format(Locale.getDefault(),"%.2f",km));


        if (goalCalorie / 4 < calorie) {

            progress = progress + 25;

            binding.calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.red));
            binding.calorieProgress.setProgress((int) ((float)calorie/goalCalorie*10));
        }
        if (goalTime / 4 < min) {
            progress = progress + 25;
            binding.exerciseProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.red));
            binding.exerciseProgress.setProgress((int) ((float)min/goalTime*10));
        }

        if (goalKm / 4 < km) {
            progress = progress + 25;
            binding.kmProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.red));
            binding.kmProgress.setProgress((int) ((float)km/goalKm*10));
        }


        if (goalCalorie / 2 < calorie) {

            progress = progress + 25;

            binding.calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.yellow));
            binding.calorieProgress.setProgress((int) ((float)calorie/goalCalorie*10));
        }
        if (goalTime / 2 < min) {
            progress = progress + 25;
            binding.exerciseProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.yellow));
            binding.exerciseProgress.setProgress((int) ((float)min/goalTime*10));
        }

        if (goalKm / 2 < km) {
            progress = progress + 25;
            binding.kmProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.yellow));
            binding.kmProgress.setProgress((int) ((float)km/goalKm*10));
        }

        if (goalCalorie <= calorie) {
            progress = progress + 8;
            binding.calorieProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.green));
            binding.calorieProgress.setProgress(10);
        }
        if (goalTime <= min) {
            progress = progress + 8;
            binding.exerciseProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.green));
            binding.exerciseProgress.setProgress(10);
        }
        if (goalKm <= km) {
            progress = progress + 8;
            binding.kmProgress.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.green));
            binding.kmProgress.setProgress(10);
        }
    }


}
