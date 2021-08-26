package com.example.survirun.Fragmnet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ExerciseData;
import com.example.survirun.R;
import com.example.survirun.RecyclerViewAdapter;
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

        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(recyclerViewAdapter);

        View view = binding.getRoot();
        return view;
    }
    void  ExerciseList(){
        switch (count){
            case 1:{
                binding.exerciseTypeTextView.setText("페이스목표");
                mList=new ArrayList<>();
                UpData("가볍게 걷기",30,0);
                UpData("꾸준히 걷기",50,0);
                UpData("가볍게 달리기",30,0);
                UpData("힘차게 달리기",30,0);
                UpData("활기차게 걷기",40,0);
                UpData("지방태우기",60,0);
                break;


            }
            case 2:{
                ListChange("운동시간 목표");
                UpData("30분 운동하기",30,0);
                UpData("1시간 운동하기",60,0);
                UpData("1시간 30분운동하기",90,0);
                UpData("2시간 운동하기",120,0);
                UpData("2시간 30분 운동하기",150,0);
                break;


            }
            case 3:{
                ListChange("운동 거리 목표");
                UpData("1Km",0,0);
                UpData("2Km",0,0);
                UpData("3Km",0,0);
                UpData("4km",0,0);
                UpData("5km",0,0);
                break;

            }
            case 4:{
                ListChange("칼로리 소모 목표");
                UpData("300칼로리",0,300);
                UpData("400칼로리",0,400);
                UpData("500칼로리",0,500);
                UpData("600칼로리",0,600);
                UpData("700칼로리",0,700);
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
        binding.exerciseTypeTextView.setText(title);
        mList.clear();
        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(recyclerViewAdapter);

    }

    void UpData(String exerciseName,int hour ,int calorie){

        ExerciseData exerciseData=new ExerciseData();
        exerciseData.setExName(exerciseName);
        exerciseData.setHour(hour);
        exerciseData.getCalorie(calorie);
        mList.add(exerciseData);

    }

   }