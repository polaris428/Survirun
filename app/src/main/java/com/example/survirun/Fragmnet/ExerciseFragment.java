package com.example.survirun.Fragmnet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
    ArrayList<ExerciseData> mList=new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(mList);
        binding.recyclerView.setAdapter(recyclerViewAdapter);
        UpData("가볍게 걷기",30);
        UpData("꾸준히 걷기",50);
        UpData("가볍게 달리기",30);
        UpData("힘차게 달리기",30);
        UpData("활기차게 걷기",40);
        UpData("지방태우기",60);


        View view = binding.getRoot();
        return view;
    }
    void UpData(String exerciseName,int hour ){

        ExerciseData exerciseData=new ExerciseData();
        exerciseData.setExName(exerciseName);
        exerciseData.setHour(hour);
        mList.add(exerciseData);

    }

   }