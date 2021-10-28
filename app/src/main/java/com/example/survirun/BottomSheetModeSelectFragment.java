package com.example.survirun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
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
    boolean isCheckZombie = false,
            isCheckGps = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBottomSheetModeSelectBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mListener = (BottomSheetListener) getContext();
        Intent intent = new Intent(getContext(), ExplanationActivity.class);

        binding.cancelButton.setOnClickListener(v1 -> {
            mListener.onClickCancel();
        });

        binding.exerciseStartButton.setOnClickListener(v1 -> {
            mListener.onClickStart();
        });

        binding.zombieChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onCheckZombie(isChecked);
            isCheckZombie = isChecked;
            if(isChecked){
                if (getActivity() == null) {
                    return;
                }
                binding.zombieChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.red)));
                binding.zombieChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
            else{
                binding.zombieChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.brightGray)));
                binding.zombieChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }
            setButtonColor();
        });

        binding.zombieChip.setOnLongClickListener(v1 -> {
            intent.putExtra("info", "좀비모드를 킬시 맵에 좀비들이 달려옵니다 ");
            startActivity(intent);
            return true;
        });

        binding.gpsChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mListener.onCheckGps(isChecked);
            isCheckGps = isChecked;
            if(isChecked){
                if (getActivity() == null) {
                    return;
                }
                binding.gpsChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.red)));
                binding.gpsChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
            else{
                binding.gpsChip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.brightGray)));
                binding.gpsChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }
            setButtonColor();
        });

        binding.gpsChip.setOnLongClickListener(v1 -> {
            intent.putExtra("info", "GPS를 킬시 내가 구글맵을 통해 다양한 기능을 제공 받을 수 있습니다 GPS를 끌시 걸음수로 운동을 특정합니다 ");
            startActivity(intent);
            return true;
        });
        // Inflate the layout for this fragment
        return v;

    }

    @SuppressLint("ResourceAsColor")
    private void setButtonColor(){
        if(isCheckZombie||isCheckGps){
            if (getActivity() == null) {
                return;
            }
            binding.exerciseStartButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
        }
        else{
            if (getActivity() == null) {
                return;
            }
            binding.exerciseStartButton.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.gray));
        }
    }

    public interface BottomSheetListener {
        void onClickCancel();

        void onClickStart();

        void onCheckZombie(boolean isCheck);

        void onCheckGps(boolean isCheck);
    }
}