package com.example.survirun.Fragmnet;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.Activity.UserGoalActivity;
import com.example.survirun.UserModel;
import com.example.survirun.databinding.FragmentUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public  String uid= FirebaseAuth.getInstance().getUid();
    private static final String DEFAULT_PATTERN = "%d%%";

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
    UserModel userModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view=binding.getRoot();

        List<UserModel> userModels=new ArrayList<>();
        userModels.clear();

        FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel=snapshot.getValue(UserModel.class);
                userModels.add(snapshot.getValue(UserModel.class));
                binding.tvKm.setText(userModel.todayKm+"");
                binding.tvCalorie.setText(userModel.todayCalorie+"");
                binding.tvTime.setText(userModel.todayExerciseTime+"");
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