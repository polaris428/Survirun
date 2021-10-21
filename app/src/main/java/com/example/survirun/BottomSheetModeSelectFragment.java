package com.example.survirun;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.databinding.FragmentBottomSheetModeSelectBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetModeSelectFragment extends BottomSheetDialogFragment {
    FragmentBottomSheetModeSelectBinding binding;
    private BottomSheetListener mListener;
    boolean isCheckZombie = false,
            isCheckGps = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBottomSheetModeSelectBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mListener = (BottomSheetListener) getContext();
        binding.cancelButton.setOnClickListener(v1 -> {
            mListener.onClickCancel();
        });

        binding.exerciseStartButton.setOnClickListener(v1 -> {
            mListener.onClickStart();
        });

        binding.zombieChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onCheckZombie(isChecked);
            isCheckZombie = isChecked;
            setButtonColor();
        });

        binding.gpsChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onCheckGps(isChecked);
            isCheckGps = isChecked;
        });
        // Inflate the layout for this fragment
        return v;

    }

    @SuppressLint("ResourceAsColor")
    private void setButtonColor(){
        if(isCheckZombie||isCheckGps){
            binding.exerciseStartButton.setBackgroundTintList(ColorStateList.valueOf(R.color.red));
        }
        else{
            binding.exerciseStartButton.setBackgroundTintList(ColorStateList.valueOf(R.color.gray));
        }
    }

    public interface BottomSheetListener {
        void onClickCancel();

        void onClickStart();

        void onCheckZombie(boolean isCheck);

        void onCheckGps(boolean isCheck);
    }
}