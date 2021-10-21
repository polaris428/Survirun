package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.survirun.BottomSheetModeSelectFragment;
import com.example.survirun.activity.exercise.MapActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity implements BottomSheetModeSelectFragment.BottomSheetListener {
    ActivityExercisePreparationBinding binding;
    boolean isCheckedZombie = false,
            isCheckedGPS = false;
    BottomSheetModeSelectFragment selectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectFragment = new BottomSheetModeSelectFragment();
        Intent intent = new Intent(ExercisePreparationActivity.this, ExplanationActivity.class);

        binding.exerciseStartButton.setOnClickListener(v -> {
            selectFragment.show(getSupportFragmentManager(),"bottomSheet");
        });


        /*binding.infoZombieButton.setOnClickListener(v -> {
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
        */
    }

    @Override
    public void onClickCancel() {
        selectFragment.dismiss();
    }

    @Override
    public void onClickStart() {
        if(isCheckedZombie||isCheckedGPS){
            ArrayList<Integer> modeList = new ArrayList();
            if (isCheckedZombie) modeList.add(MapActivity.ZOMBIE_MODE);
                //else if (isCheckedStory) modeList.add(MapActivity.STORY_MODE);
            else modeList.add(MapActivity.DEFAULT_MODE);
            Intent i = new Intent(ExercisePreparationActivity.this, MapActivity.class);
            i.putExtra("mode", modeList);
            Log.d("asdf", String.valueOf(modeList));
            startActivity(i);
        }
        else {
            Toast.makeText(getApplicationContext(), "모드를 선택해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckZombie(boolean isCheck) {
        isCheckedZombie = isCheck;
    }

    @Override
    public void onCheckGps(boolean isCheck) {
        isCheckedGPS = isCheck;
    }
}