package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.Activity.UserGoalActivity;
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
    String uid= FirebaseAuth.getInstance().getUid();
    Date currentTime = Calendar.getInstance().getTime();

    int progress=0;

    UserModel userModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view=binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        int goalCalorie = sf.getInt("calorie", 400);
        int goalTime = sf.getInt("time", 60);
        int goalkm = sf.getInt("km", 5);
        String date = new SimpleDateFormat("(yy.MM.dd)", Locale.getDefault()).format(currentTime);
        binding.tvDate.setText(date);
        List<UserModel> userModels=new ArrayList<>();
        userModels.clear();

        FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel=snapshot.getValue(UserModel.class);
                userModels.add(snapshot.getValue(UserModel.class));
                binding.kmTextView.setText(userModel.todayKm+"");
                binding.calorieTextView.setText(userModel.todayCalorie+"");
                binding.timeTextView.setText(userModel.todayExerciseTime+"");
                if(goalCalorie==userModel.todayCalorie){
                    progress=progress+33;
                    binding.arcProgress.setProgress(progress);
                }
                if(goalTime==userModel.todayExerciseTime){
                    progress=progress+33;
                    binding.arcProgress.setProgress(progress);
                }
                if(goalkm==userModel.todayKm){
                    progress=progress+33;
                    binding.arcProgress.setProgress(progress);
                }
                if(progress==99){
                    progress=100;
                    binding.arcProgress.setProgress(progress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        binding.btnGoal.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), UserGoalActivity.class);
            startActivity(intent);
        });

        return view ;
    }

}