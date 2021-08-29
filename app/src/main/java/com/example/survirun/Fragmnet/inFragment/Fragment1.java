package com.example.survirun.Fragmnet.inFragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.MyMarkerView;
import com.example.survirun.R;

import com.example.survirun.databinding.Fragment1Binding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment {
    Fragment1Binding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment1Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.goalBarGraph.clearChart();
        binding.goalBarGraph.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.goalBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.goalBarGraph.startAnimation();

        binding.calorieBarGraph.clearChart();
        binding.calorieBarGraph.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.calorieBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));

        binding.calorieBarGraph.startAnimation();

        binding.exerciseTimeBarGraph.clearChart();

        binding.exerciseTimeBarGraph.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.exerciseTimeBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));

        binding.exerciseTimeBarGraph.startAnimation();

        binding.kmBarGraph.clearChart();

        binding.kmBarGraph.addBar(new BarModel("12", 10f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("14", 10f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("15", 20f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("16", 10f, 0xFF56B7F1));
        binding.kmBarGraph.addBar(new BarModel("17", 10f, 0xFF56B7F1));

        binding.kmBarGraph.startAnimation();




        return view;
    }
}