package com.example.survirun.activity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    String email;
    String pwe;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        editor = sf.edit();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        verifyStoragePermissions(this);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (!email.equals("")) {
                    login();

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000); // 0.5초후


    }

    public void login() {
        LoginData loginData = new LoginData(email, pwe);
        Call<TokenData> call = ServerClient.getServerService().login(loginData);
        call.enqueue(new Callback<TokenData>() {
            @Override
            public void onResponse(Call<TokenData> call, Response<TokenData> response) {

                if (response.isSuccessful()) {
                    editor.putString("email", email);
                    editor.putString("pwe", pwe);
                    editor.putString("token", response.body().token);
                    editor.commit();
                    Intent intent;

                    if (!response.body().username) {
                        Log.d("token", response.body().token);
                        intent = new Intent(SplashActivity.this, SignUpNameActivity.class);
                    } else {
                        if (response.body().profile) {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, SignUpProfileActivity.class);
                        }

                    }
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<TokenData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}

