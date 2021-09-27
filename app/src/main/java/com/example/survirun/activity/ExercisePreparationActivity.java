package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.survirun.activity.exercise.MapActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity {
    ActivityExercisePreparationBinding binding;
    boolean isCheckedZombie = false,
            isCheckedStory = false,
            isCheckedGPS = false,
            isCheckedNo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = new Intent(ExercisePreparationActivity.this, ExplanationActivity.class);

        binding.infoZombieButton.setOnClickListener(v -> {
            intent.putExtra("info", "좀비모드를 킬시 맵에 좀비들이 달려옵니다 ");
            startActivity(intent);
        });

        binding.infoStoryButton.setOnClickListener(v -> {
            intent.putExtra("info", "스토리 모드를 켤시 운동중 스토리 타운에 도착하면 좀비런의 스토리를 플레이 할 수 있습니다");
            startActivity(intent);
        });

        binding.infoGpsButton.setOnClickListener(v -> {
            intent.putExtra("info", "GPS를 킬시 내가 구글맵을 통해 다양한 기능을 제공 받을 수 있습니다 GPS를 끌시 걸음수로 운동을 특정합니다 ");
            startActivity(intent);
        });
        binding.infoNoButton.setOnClickListener(v -> {
            intent.putExtra("info", "no");
            startActivity(intent);
        });
        binding.zombieSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckedZombie = isChecked;
            if (isChecked) {
                binding.storySwitch.setChecked(false);
                binding.gpsSwitch.setChecked(false);
                binding.noSwitch.setChecked(false);
            }
        });
        binding.storySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckedStory = isChecked;
            if (isChecked) {
                binding.zombieSwitch.setChecked(false);
                binding.gpsSwitch.setChecked(false);
                binding.noSwitch.setChecked(false);
            }
        });
        binding.gpsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckedGPS = isChecked;
            if (isChecked) {
                binding.zombieSwitch.setChecked(false);
                binding.storySwitch.setChecked(false);
                binding.noSwitch.setChecked(false);
            }
        });
        binding.noSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckedNo = isChecked;
            if (isChecked) {
                binding.zombieSwitch.setChecked(false);
                binding.storySwitch.setChecked(false);
                binding.gpsSwitch.setChecked(false);
            }
        });
        binding.exerciseStartButton.setOnClickListener(v -> {
            ArrayList<Integer> modeList = new ArrayList();
            if (isCheckedZombie) modeList.add(MapActivity.ZOMBIE_MODE);
            else if (isCheckedStory) modeList.add(MapActivity.STORY_MODE);
            else modeList.add(MapActivity.DEFAULT_MODE);
            Intent i = new Intent(ExercisePreparationActivity.this, MapActivity.class);
            i.putExtra("mode", modeList);
            Log.d("asdf", String.valueOf(modeList));
            startActivity(i);
        });
    }
}