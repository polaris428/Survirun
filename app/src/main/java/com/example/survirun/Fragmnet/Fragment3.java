package com.example.survirun.Fragmnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.survirun.databinding.Fragment3Binding;


public class Fragment3 extends Fragment {
    Fragment3Binding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment3Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
}