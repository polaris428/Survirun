package com.example.survirun.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityUserGoalModifyBinding;

public class UserGoalModifyActivity extends AppCompatActivity {
    private ActivityUserGoalModifyBinding binding;
    boolean isCalorie = true;
    boolean isTimeH = true;
    boolean isTimeM = true;
    boolean isKm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserGoalModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("goal", MODE_PRIVATE);
        int calorie = sf.getInt("calorie", 400);
        int time = sf.getInt("time", 60);
        int km = sf.getInt("km", 5);

        binding.etCalorie.setText(String.valueOf(calorie));
        binding.etH.setText(String.valueOf(time/60));
        binding.etM.setText(String.valueOf(time%60));
        binding.etKm.setText(String.valueOf(km));

        binding.etH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(Integer.parseInt(binding.etH.getText().toString())>23) binding.etH.setText(String.valueOf(23));
                } catch (Exception e) {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(binding.etH.getText().toString().replace(" ", "").equals("")) isTimeH = false;
                else isTimeH = true;
            }
        });
        binding.etM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(Integer.parseInt(binding.etM.getText().toString())>59) binding.etM.setText(String.valueOf(59));
                } catch (Exception e) {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(binding.etM.getText().toString().replace(" ", "").equals("")) isTimeM = false;
                else isTimeM = true;
            }
        });
        binding.etKm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(binding.etKm.getText().toString().replace(" ", "").equals("")) isKm = false;
                else isKm = true;
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if(binding.etCalorie.getText().toString().replace(" ", "").equals("")) isCalorie = false;
            else isCalorie = true;
            if(isCalorie&&isTimeH&&isTimeM&&isKm){
                int inputCalorie = Integer.parseInt(binding.etCalorie.getText().toString());
                int inputTime = Integer.parseInt(binding.etH.getText().toString())*60+Integer.parseInt(binding.etM.getText().toString());
                int inputKm = Integer.parseInt(binding.etKm.getText().toString());
                SharedPreferences.Editor editor= sf.edit();
                editor.putInt("calorie", inputCalorie);
                editor.putInt("time", inputTime);
                editor.putInt("km", inputKm);
                editor.apply();
                editor.commit();
                Intent intent = new Intent(UserGoalModifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "빈칸을 채워주세요", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserGoalModifyActivity.this, UserGoalActivity.class);
            startActivity(intent);
        });

    }
}