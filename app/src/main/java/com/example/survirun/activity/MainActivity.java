package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.survirun.Fragmnet.ExerciseFragment;
import com.example.survirun.Fragmnet.Friend.FriendFragment;
import com.example.survirun.Fragmnet.SettingFragment;
import com.example.survirun.R;
import com.example.survirun.Fragmnet.StatisticsFragment;
import com.example.survirun.Fragmnet.UserFragment;
import com.example.survirun.databinding.ActivityMainBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic__graphicon));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_walking));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_friend));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_friend));
        binding.meowBottomNavigation.show(1, true);
        replace(new UserFragment());
        binding.meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        replace(new UserFragment());
                        break;
                    case 2:
                        replace(new StatisticsFragment());
                        break;
                    case 3:
                        replace(new ExerciseFragment());
                        break;

                    case 4:
                        replace(new FriendFragment());
                        break;
                    case 5:
                        replace(new SettingFragment());
                        break;
                }
                return null;
            }
        });

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}