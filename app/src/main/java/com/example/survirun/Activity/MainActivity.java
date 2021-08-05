package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityMainBinding;
import com.example.survirun.databinding.ActivitySingUpBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.common_full_open_on_phone));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.common_google_signin_btn_icon_dark_focused));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.common_google_signin_btn_icon_dark_normal_background));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_launcher_background));
    }
}