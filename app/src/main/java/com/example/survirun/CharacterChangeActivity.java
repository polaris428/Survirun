package com.example.survirun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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

        binding.characterPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position==0){
                    binding.leftButton.setVisibility(View.GONE);
                    binding.circleImageView.setImageResource(R.drawable.ic_circle_1);
                }
                else if(position==1){
                    binding.leftButton.setVisibility(View.VISIBLE);
                    binding.rightButton.setVisibility(View.VISIBLE);
                    binding.circleImageView.setImageResource(R.drawable.ic_circle_2);
                }
                else if(position==2){
                    binding.leftButton.setVisibility(View.VISIBLE);
                    binding.rightButton.setVisibility(View.VISIBLE);
                    binding.circleImageView.setImageResource(R.drawable.ic_circle_3);
                }
                else if(position==3){
                    binding.rightButton.setVisibility(View.GONE);
                    binding.circleImageView.setImageResource(R.drawable.ic_circle_4);
                }
            }
        });
        FragmentStateAdapter welcomePagerAdapter = new ScreenSlidePagerAdapter(this, characterLayouts);
        binding.characterPager.setAdapter(welcomePagerAdapter);

    }
}