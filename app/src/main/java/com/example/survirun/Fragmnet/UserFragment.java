package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.UserPageActivity;
import com.example.survirun.activity.UserGoalActivity;
import com.example.survirun.Medel.UserModel;
import com.example.survirun.databinding.FragmentUserBinding;
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

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    String uid = FirebaseAuth.getInstance().getUid();
    Date currentTime = Calendar.getInstance().getTime();

    int progress = 0;
    UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        int goalCalorie = sf.getInt("calorie", 400);
        int goalTime = sf.getInt("time", 60);
        int goalkm = sf.getInt("km", 5);
        String date = new SimpleDateFormat("(yy.MM.dd)", Locale.getDefault()).format(currentTime);
        binding.dateTextview.setText(date);
        List<UserModel> userModels = new ArrayList<>();
        userModels.clear();

        binding.userPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserPageActivity.class);
            startActivity(intent);
        });

        FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);
                userModels.add(snapshot.getValue(UserModel.class));
                String km=String.format("%.2f",userModel.score.todayKm);
                binding.kmTextview.setText(km);
                binding.calorieTextview.setText(userModel.score.todayCalorie + "");
                binding.timeTextview.setText(userModel.score.todayExerciseTime + "");
                if (goalCalorie / 2 < userModel.score.todayCalorie) {
                    progress = progress + 25;
                    binding.calorieCardView.setCardBackgroundColor(Color.YELLOW);
                }
                if (goalTime / 2 < userModel.score.todayExerciseTime) {
                    progress = progress + 25;
                    binding.exerciseTimeCardView.setCardBackgroundColor(Color.YELLOW);
                }
                if (goalkm / 2 < userModel.score.todayKm) {
                    progress = progress + 25;
                    binding.kmCardView.setCardBackgroundColor(Color.YELLOW);
                }

                if (goalCalorie <= userModel.score.todayCalorie) {
                    progress = progress + 8;
                    binding.arcProgress.setProgress(progress);
                    binding.calorieCardView.setCardBackgroundColor(Color.GREEN);
                }
                if (goalTime <= userModel.score.todayExerciseTime) {
                    progress = progress + 8;
                    binding.arcProgress.setProgress(progress);
                    binding.exerciseTimeCardView.setCardBackgroundColor(Color.GREEN);
                }
                if (goalkm <= userModel.score.todayKm) {
                    progress = progress + 8;
                    binding.arcProgress.setProgress(progress);
                    binding.kmCardView.setCardBackgroundColor(Color.GREEN);
                }
                if (progress >= 99) {
                    progress = 100;
                    binding.arcProgress.setProgress(progress);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        binding.goalButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserGoalActivity.class);
            startActivity(intent);
        });

        return view;
    }

}