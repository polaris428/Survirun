package com.example.survirun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.activity.ExplanationActivity;
import com.example.survirun.databinding.FragmentBottomSheetModeSelectBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetModeSelectFragment extends BottomSheetDialogFragment {
    FragmentBottomSheetModeSelectBinding binding;
    private BottomSheetListener mListener;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBottomSheetModeSelectBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mListener = (BottomSheetListener) getContext();


        binding.exerciseStartButton.setOnClickListener(v1 -> {
            mListener.onClickStart();
        });


        // Inflate the layout for this fragment
        return v;

    }


    public interface BottomSheetListener {

        void onClickStart();
    }
}