package com.example.survirun.activity.exercise;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.survirun.BottomSheetModeSelectFragment;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.databinding.ActivityExercisePreparationBinding;

import java.util.ArrayList;

public class ExercisePreparationActivity extends AppCompatActivity implements BottomSheetModeSelectFragment.BottomSheetListener {
    ActivityExercisePreparationBinding binding;
    BottomSheetModeSelectFragment selectFragment;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String title;
    String calorie;
    String km;
    String min;
    String hour;
    int level;
    int zombieCountIntent;
    int time;

    boolean zombieMode = true;
    boolean isCheckLevel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExercisePreparationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkGPSPermission();
        selectFragment = new BottomSheetModeSelectFragment();

        Intent exerciseSelection = getIntent();
        title = exerciseSelection.getStringExtra("title");
        calorie = exerciseSelection.getStringExtra("calorie");
        km = exerciseSelection.getStringExtra("km");
        hour = exerciseSelection.getStringExtra("hour");
        min = exerciseSelection.getStringExtra("min");
        level = exerciseSelection.getIntExtra("level", 1);

        int ran = (int) ((Math.random() * 10000) % 10) + 1;
        if (ran == 11) ran = 1;
        binding.proverbTextView.setText(R.string.saying_ + ran);

        binding.exerciseTitleTextview.setText(title);
        binding.calorieTextView.setText(calorie);
        if (hour.equals("0")) {
            binding.hourTextView.setVisibility(View.GONE);
            binding.hourUnitTextView.setVisibility(View.GONE);

        } else {
            binding.hourTextView.setText(hour);
        }
        if (min.equals("0") && hour.equals("0")) {
            binding.minTextView.setText("0");
        } else if (min.equals("0")) {
            binding.minTextView.setVisibility(View.GONE);
            binding.minUnitTextView.setVisibility(View.GONE);

        } else {
            binding.minTextView.setText(min);
        }


        binding.kmTextView.setText(km);

        setAnimation(binding.constraint);

        binding.exerciseStartButton.setOnClickListener(v -> {
            selectFragment.show(getSupportFragmentManager(), "bottomSheet");

        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
        time = Integer.parseInt(hour);
        time = time * 60 + Integer.parseInt(min);
        Log.d("time", time + "");
    }

    @Override
    public void onClickStart() {

        if (!isCheckLevel) {
            Toast.makeText(getApplicationContext(), R.string.choose_level, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Integer> modeList = new ArrayList();
            Intent intent = new Intent(ExercisePreparationActivity.this, MapActivity.class);

            intent.putExtra("title", title);
            intent.putExtra("calorie", Integer.parseInt(calorie));
            intent.putExtra("km", Double.parseDouble(km));
            intent.putExtra("time", time);
            intent.putExtra("min", min);
            intent.putExtra("level", level);
            intent.putExtra("zombieCount", zombieCountIntent);
            intent.putExtra("zombieMode", zombieMode);

            Log.d("asdf", String.valueOf(modeList));
            startActivity(intent);
        }
    }

    @Override
    public void onCheckZombie(boolean isCheck) {
        zombieMode = isCheck;
    }

    @Override
    public void onCheckLevel(int zombieCount, boolean isCheckLed) {
        zombieCountIntent = zombieCount;
        isCheckLevel = isCheckLed;
        Log.d("asdf", String.valueOf(zombieCount));
    }


    private void setAnimation(ConstraintLayout constraintLayout) {
        ValueAnimator anim = ValueAnimator.ofInt(1, 1000);
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            constraintLayout.getLayoutParams().height = value.intValue();
            constraintLayout.requestLayout();
        });
        anim.start();
    }
    private void checkGPSPermission() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(ExercisePreparationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(ExercisePreparationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ExercisePreparationActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(ExercisePreparationActivity.this, R.string.need_location, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(ExercisePreparationActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExercisePreparationActivity.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.disable_location);
        builder.setMessage("위치 서비스를 활성화 시켜주세요");
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(ExercisePreparationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        builder.create().show();
    }

}