package com.example.survirun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.View;

import com.example.survirun.activity.exercise.ZoomOutPageTransformer;
import com.example.survirun.databinding.ActivityCharacterChangeBinding;
import com.example.survirun.fragmnet.ScreenSlidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CharacterChangeActivity extends AppCompatActivity {
    ActivityCharacterChangeBinding binding;
    List<Integer> characterLayouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharacterChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.characterPager.setPageTransformer(new ZoomOutPageTransformer());
        characterLayouts = new ArrayList<>();
        characterLayouts.add(R.layout.fragment_character1);
        characterLayouts.add(R.layout.fragment_character2);
        characterLayouts.add(R.layout.fragment_character3);
        characterLayouts.add(R.layout.fragment_character4);

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        binding.leftButton.setOnClickListener(v -> {
            int current = binding.characterPager.getCurrentItem();
            if(current != 0){
                binding.characterPager.setCurrentItem(current-1);
            }

        });
        binding.rightButton.setOnClickListener(v -> {
            int current = binding.characterPager.getCurrentItem();
            if(current!=3){
                binding.characterPager.setCurrentItem(current+1);
            }
        });
        FragmentStateAdapter welcomePagerAdapter = new ScreenSlidePagerAdapter(this, characterLayouts);
        binding.characterPager.setAdapter(welcomePagerAdapter);

    }
}