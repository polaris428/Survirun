package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkFirst();

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic__graphicon));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_walking));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_friend));
        binding.meowBottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_seetting));
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
                    default:
                        replace(new UserFragment());
                        break;

                }
                return null;
            }
        });


    }

    private void checkFirst(){
        SharedPreferences sharedPreferences = getSharedPreferences("checkFirstAccess", MODE_PRIVATE);
        boolean checkFirstAccess = sharedPreferences.getBoolean("checkFirstAccess", false);

        if (!checkFirstAccess) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("checkFirstAccess", true);
            editor.apply();
            Intent tutorialIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(tutorialIntent);
        }
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> finishAffinity());
    }
}