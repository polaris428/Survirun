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
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.server.ServerClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    String email;
    String pwe;
    String name;
    Boolean profile;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        name=sf.getString("name","");
        profile=sf.getBoolean("profile",false);
        editor=sf.edit();
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if(!email. equals("")){
                    login();

                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000); // 0.5초후


    }
    public void login(){
        LoginData loginData=new LoginData(email,pwe);
        Call<TokenData> call= ServerClient.getServerService().login(loginData);
        call.enqueue(new Callback<TokenData>() {
            @Override
            public void onResponse(Call<TokenData> call, Response<TokenData> response) {

                if(response.isSuccessful()) {
                    editor.putString("email", email);
                    editor.putString("pwe",pwe);
                    editor.putString("token",response.body().token);
                    editor.commit();
                    Intent intent;

                    if(name.equals("")){
                        Log.d("token",response.body().token);
                        Log.d("name",name);
                        intent = new Intent(SplashActivity.this, SignUpNameActivity.class);
                    }else {
                        if(profile==true){
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        }else{
                            intent = new Intent(SplashActivity.this, SignUpProfileActivity.class);
                        }

                    }
                    startActivity(intent);

                }else{
                    Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<TokenData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



}

