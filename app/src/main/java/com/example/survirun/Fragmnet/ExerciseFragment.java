package com.example.survirun.Fragmnet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ExerciseData;
import com.example.survirun.R;
import com.example.survirun.activity.exercise.ExerciseListRecyclerViewAdapter;
import com.example.survirun.databinding.FragmentExerciseBinding;

import java.util.ArrayList;


public class ExerciseFragment extends Fragment {
    FragmentExerciseBinding binding;


    ArrayList<ExerciseData> mList;
    int count = 1;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        ExerciseList();
        binding.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                ExerciseList();
            }
        });
        binding.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                ExerciseList();
            }
        });

        ExerciseListRecyclerViewAdapter exerciseListRecyclerViewAdapter = new ExerciseListRecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(exerciseListRecyclerViewAdapter);

        View view = binding.getRoot();
        return view;
    }

    void ExerciseList() {
        switch (count) {

            case 1: {
                binding.exerciseTypeTextview.setText(R.string.pace_goal);
                mList = new ArrayList<>();
                UpData(getString(R.string.walking_lightly), 15, 50,1,1);
                UpData(getString(R.string.walking_briskly), 30, 100,2,1);
                UpData(getString(R.string.walking_steadily), 60, 200,3,2);
                UpData(getString(R.string.burning_fat), 90, 300,4,3);
                break;

            }
            case 2: {
                ListChange(getString(R.string.exercise_time_goal));
                UpData("30" + getString(R.string.minute_exercise), 30, 0,0,1);
                UpData("1" + getString(R.string.hour_exercise), 60, 0,0,2);
                UpData("1" + getString(R.string.hour) + "30" + getString(R.string.minute_exercise), 90, 0,0,3);
                UpData("2" + getString(R.string.hour_exercise), 120, 0,0,4);
                UpData("2" + getString(R.string.hour) + "30" + getString(R.string.minute_exercise), 150, 0,0,4);
                break;

            }
            case 3: {
                ListChange(getString(R.string.exercise_distance_goal));
                UpData("1Km", 0, 0,1,1);
                UpData("2Km", 0, 0,2,1);
                UpData("3Km", 0, 0,3,2);
                UpData("4km", 0, 0,4,3);
                UpData("5km", 0, 0,5,3);
                break;

            }
            case 4: {
                ListChange(getString(R.string.calorie_burning_goal));
                UpData("300kcal", 0, 300,0,3);
                UpData("400kcal", 0, 400,0,3);
                UpData("500kcal", 0, 500,0,4);
                UpData("600kcal", 0, 600,0,4);
                UpData("700kcal", 0, 700,0,5);
                break;

            }
            default:
                if (count == 0) {
                    count=1;
                    UpData(getString(R.string.walking_lightly), 15, 50,1,1);
                    UpData(getString(R.string.walking_briskly), 30, 100,2,1);
                    UpData(getString(R.string.walking_steadily), 60, 200,3,2);
                    UpData(getString(R.string.burning_fat), 90, 300,4,3);

                } else if (count==5){
                    count=4;
                    ListChange(getString(R.string.calorie_burning_goal));
                    UpData("300kcal", 0, 300,0,3);
                    UpData("400kcal", 0, 400,0,3);
                    UpData("500kcal", 0, 500,0,4);
                    UpData("600kcal", 0, 600,0,4);
                    UpData("700kcal", 0, 700,0,5);
                }
        }

    }

    void ListChange(String title) {
        binding.exerciseTypeTextview.setText(title);
        mList.clear();
        ExerciseListRecyclerViewAdapter exerciseListRecyclerViewAdapter = new ExerciseListRecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(exerciseListRecyclerViewAdapter);
    }

    void UpData(String exerciseName, int hour, int calorie, int km ,int level) {
        ExerciseData exerciseData = new ExerciseData();
        exerciseData.setExName(exerciseName);
        exerciseData.setHour(hour);
        exerciseData.setCalorie(calorie);
        exerciseData.setKm(km);
        exerciseData.setLevel(level);
        mList.add(exerciseData);
    }

}