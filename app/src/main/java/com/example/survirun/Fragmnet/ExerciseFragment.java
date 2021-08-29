package com.example.survirun.Fragmnet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ExerciseData;
import com.example.survirun.R;
import com.example.survirun.ExerciseListRecyclerViewAdapter;
import com.example.survirun.databinding.FragmentExerciseBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment {
    FragmentExerciseBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseFragment newInstance(String param1, String param2) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ArrayList<ExerciseData> mList;
    int count=1;
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

        ExerciseListRecyclerViewAdapter exerciseListRecyclerViewAdapter =new ExerciseListRecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(exerciseListRecyclerViewAdapter);

        View view = binding.getRoot();
        return view;
    }
    void  ExerciseList(){
        switch (count){
            case 1:{
                binding.exerciseTypeTextview.setText(R.string.pace_goal);
                mList=new ArrayList<>();
                UpData(getString(R.string.walking_lightly),30,0);
                UpData(getString(R.string.walking_steadily),50,0);
                UpData(getString(R.string.running_lightly),30,0);
                UpData(getString(R.string.running_vigorously),30,0);
                UpData(getString(R.string.walking_briskly),40,0);
                UpData(getString(R.string.burning_fat),60,0);
                break;


            }
            case 2:{
                ListChange(getString(R.string.exercise_time_goal));
                UpData("30"+getString(R.string.minute_exercise),30,0);
                UpData("1"+getString(R.string.hour_exercise),60,0);
                UpData("1"+getString(R.string.hour)+"30"+getString(R.string.minute_exercise),90,0);
                UpData("2"+getString(R.string.hour_exercise),120,0);
                UpData("2"+getString(R.string.hour)+"30"+getString(R.string.minute_exercise),150,0);
                break;


            }
            case 3:{
                ListChange(getString(R.string.exercise_distance_goal));
                UpData("1Km",0,0);
                UpData("2Km",0,0);
                UpData("3Km",0,0);
                UpData("4km",0,0);
                UpData("5km",0,0);
                break;

            }
            case 4:{
                ListChange(getString(R.string.calorie_burning_goal));
                UpData("300kcal",0,300);
                UpData("400kcal",0,400);
                UpData("500kcal",0,500);
                UpData("600kcal",0,600);
                UpData("700kcal",0,700);
                break;

            }
            default:
                if (count==0){
                    count++;
                }else {
                    count--;
                }
        }

    }
    void ListChange(String title){
        binding.exerciseTypeTextview.setText(title);
        mList.clear();
        ExerciseListRecyclerViewAdapter exerciseListRecyclerViewAdapter =new ExerciseListRecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(exerciseListRecyclerViewAdapter);

    }

    void UpData(String exerciseName,int hour ,int calorie){

        ExerciseData exerciseData=new ExerciseData();
        exerciseData.setExName(exerciseName);
        exerciseData.setHour(hour);
        exerciseData.getCalorie(calorie);
        mList.add(exerciseData);

    }

   }