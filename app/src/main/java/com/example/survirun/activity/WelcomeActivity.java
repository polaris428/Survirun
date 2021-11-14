package com.example.survirun.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;

import com.example.survirun.R;
import com.example.survirun.ScreenSlidePagerAdapter;
import com.example.survirun.ZoomOutPageTransformer;
import com.example.survirun.databinding.ActivityWelcomeBinding;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends FragmentActivity {
    ActivityWelcomeBinding binding;
    List<Integer> welcomeLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.welcomeViewPager.setPageTransformer(new ZoomOutPageTransformer());
        welcomeLayouts = new ArrayList<>();
        welcomeLayouts.add(R.layout.welcome_1);
        welcomeLayouts.add(R.layout.welcome_2);
        welcomeLayouts.add(R.layout.welcome_3);

        FragmentStateAdapter welcomePagerAdapter = new ScreenSlidePagerAdapter(this, welcomeLayouts);
        binding.welcomeViewPager.setAdapter(welcomePagerAdapter);

        binding.skipButton.setOnClickListener(v -> {
            finish();
        });
    }
}