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
    SharedPreferences goal;
    SharedPreferences sf;

    SharedPreferences.Editor editor;
    String emile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

       // binding.drawerLayout.setDrawerListener(listener);
        goal = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        goalCalorie = goal.getInt("calorie", 400);
        goalTime = goal.getInt("time", 60);
        goalKm = goal.getInt("km", 5);
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        editor=sf.edit();
        token = sf.getString("token", "");

        name=sf.getString("name","");
        emile=sf.getString("email","");




        Call<ExerciseData> call = ServerClient.getServerService().getExercise(token);
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    binding.dateTextview.setText(response.body().date);
                    binding.calorieTextview.setText(response.body().calorie + "");
                    binding.timeTextview.setText(response.body().time + "");
                    binding.kmTextview.setText(String.format("%.2f",response.body().km));
                    editor.putString("data",response.body().date);

                    if (goalCalorie / 2 < response.body().calorie) {

                        progress = progress + 25;
                        binding.calorieCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalTime / 2 < response.body().time) {
                        progress = progress + 25;
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalKm / 2 < response.body().km) {
                        progress = progress + 25;
                        binding.kmCardView.setCardBackgroundColor(Color.YELLOW);
                    }

                    if (goalCalorie <= response.body().calorie) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.calorieCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalTime <= response.body().time) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalKm <= response.body().time) {
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



        return view;
    }

}
