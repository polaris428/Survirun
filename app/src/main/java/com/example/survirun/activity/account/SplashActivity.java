package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.survirun.Medel.UserModel;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    int today;
    int saveDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        String email = sf.getString("email", "");
        String pwe = sf.getString("pwe", "");
        saveDay = sf.getInt("day", 0);

        if(today>saveDay){
            sf.edit().putInt("day",today).commit();
            Log.d("asdf",today+"");
        }
        if (!email.equals("") && !pwe.equals("") ) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, pwe)
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {//성공했을때
                                getToday();
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SplashActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });

        } else {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }, 3000); // 0.5초후

        }



    }
    public void getToday(){
        //오늘 날짜 구하는 함수
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String getDay = sdf.format(date);
        today=Integer.parseInt(getDay);
        String uid=FirebaseAuth.getInstance().getUid();
        if(today>saveDay){
            UserModel userModel;
            userModel = new UserModel();
            userModel.todayExerciseTime = 0;
            userModel.todayKm = 0.00;
            userModel.todayCalorie = 0;
            FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).setValue(userModel);
        }



    }

}

