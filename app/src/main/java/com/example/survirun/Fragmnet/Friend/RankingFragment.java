package com.example.survirun.Fragmnet.Friend;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.survirun.activity.FriendActivity;
import com.example.survirun.databinding.FragmentRankingBinding;

public class RankingFragment extends Fragment {
    FragmentRankingBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.friendButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FriendActivity.class));
        });

        return view;
    }


}