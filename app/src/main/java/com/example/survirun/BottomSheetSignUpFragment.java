package com.example.survirun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.databinding.FragmentBottomSheetSignUpBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetSignUpFragment extends BottomSheetDialogFragment {
    FragmentBottomSheetSignUpBinding binding;
    private BottomSheetListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBottomSheetSignUpBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mListener = (BottomSheetListener) getContext();
        binding.gallery.setOnClickListener(view -> {
            mListener.onClickGallery();
        });
        binding.camera.setOnClickListener(view -> {
            mListener.onClickCamera();
        });
        return v;
    }

    public interface BottomSheetListener {
        void onClickGallery();

        void onClickCamera();
    }
}