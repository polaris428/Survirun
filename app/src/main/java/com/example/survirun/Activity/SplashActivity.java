package com.example.survirun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.survirun.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    int today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        String email = sf.getString("email", "");
        String pwe = sf.getString("pwe", "");
        getToday();
        int saveDay = sf.getInt("day", 0);

        if(today>saveDay){
            sf.edit().putInt("day",today).commit();
            Log.d("asdf",today+"");
        }
        if (email != "" && pwe != "") {
            firebaseAuth = firebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, pwe)
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {//성공했을때

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
        String getday = sdf.format(date);
        today=Integer.parseInt(getday);



    }

}

