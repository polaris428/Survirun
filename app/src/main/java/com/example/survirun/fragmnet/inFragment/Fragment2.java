package com.example.survirun.fragmnet.inFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.survirun.databinding.Fragment2Binding;

import org.eazegraph.lib.models.BarModel;

public class Fragment2 extends Fragment {
    Fragment2Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.tab1Chart2.clearChart();

        binding.tab1Chart2.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.tab1Chart2.addBar(new BarModel("17", 10f, 0xFF56B7F1));

        binding.tab1Chart2.startAnimation();


        return view;
    }
}