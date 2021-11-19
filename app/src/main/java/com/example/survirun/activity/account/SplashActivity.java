package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.survirun.data.FindUserData;
import com.example.survirun.server.NetworkStatus;
import com.example.survirun.R;
import com.example.survirun.activity.exercise.SplashActivity2;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.databinding.ActivitySplashBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    String email;
    String pwe;
    SharedPreferences.Editor editor;
    Dialog dialog;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        editor = sf.edit();

        dialog = new Dialog(SplashActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.network_dialog);

        final AnimationDrawable drawableRun =
                (AnimationDrawable) binding.runImageView.getBackground();
        drawableRun.start();
        final AnimationDrawable drawableHands =
                (AnimationDrawable) binding.handsImageView.getBackground();
        drawableHands.start();

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.run_anim);
        binding.floor.startAnimation(anim);

        network();

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        verifyStoragePermissions(this);*/

        // 0.5초후
    }

    private void network() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE) {
            Log.d("네트워크 연결 상태", "모바일로 연결");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1000);


        } else if (status == NetworkStatus.TYPE_WIFI) {
            Log.d("네트워크 연결 상태", "무선랜으로 연결됨");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1000);

        } else {
            Log.d("네트워크 연결 상태", "연결안됨");
            showDialog();

        }
    }

    public void login() {
        LoginData loginData = new LoginData(email, pwe);
        Call<TokenData> call = ServerClient.getServerService().login(loginData);
        call.enqueue(new Callback<TokenData>() {
            @Override
            public void onResponse(Call<TokenData> call, Response<TokenData> response) {

                if (response.isSuccessful()) {
                    token = response.body().token;
                    Log.d("token", token);
                    getFriendNumber();
                    editor.putString("email", email);
                    editor.putString("pwe", pwe);
                    editor.putString("token", token);
                    editor.commit();
                    UserAccount userAccount = new UserAccount();
                    userAccount.getExercise(token, SplashActivity.this);
                    userAccount.getUser(token, email, SplashActivity.this);
                    userAccount.yesterdayExercise(token, SplashActivity.this);
                    Intent intent;

                    if (!response.body().username) {

                        intent = new Intent(SplashActivity.this, SignUpNameActivity.class);

                    } else {
                        if (response.body().profile) {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, SignUpProfileActivity.class);
                        }
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    startActivity(intent);
                    overridePendingTransition(0, 0);


                } else {
                    Intent intent = new Intent(SplashActivity.this, SplashActivity2.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            // requestPermission의 배열의 index가 아래 grantResults index와 매칭
            // 퍼미션이 승인되면
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!email.equals("")) {
                    login();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {

                        Intent intent = new Intent(SplashActivity.this, SplashActivity2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    }, 2000); //딜레이 타임 조절


                }
            }
            // 퍼미션이 승인 거부되면
            else {
                if (!email.equals("")) {
                    login();
                } else {
                    Intent intent = new Intent(SplashActivity.this, SplashActivity2.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        }
    }

    void showDialog() {
        Button finishButton = dialog.findViewById(R.id.cancel_button);
        Button retryButton = dialog.findViewById(R.id.yes_button);
        Button helpButton = dialog.findViewById(R.id.help_button);

        dialog.setCancelable(false);
        dialog.show();
        helpButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("plain/text");
            String[] address = {"survirun@gmail.com"};
            email.putExtra(Intent.EXTRA_EMAIL, address);
            email.putExtra(Intent.EXTRA_SUBJECT, R.string.network_error);
            email.setPackage("com.google.android.gm");
            email.putExtra(Intent.EXTRA_TEXT, R.string.error_detail);
            startActivity(email);
        });
        finishButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        retryButton.setOnClickListener(v -> {
            dialog.dismiss();
            network();
        });
    }

    public void getFriendNumber() {
        Call<FindUserData> call = ServerClient.getServerService().getFriendList(token);
        call.enqueue(new Callback<FindUserData>() {
            @Override
            public void onResponse(Call<FindUserData> call, Response<FindUserData> response) {
                if (response.isSuccessful()) {
                    response.body().users.size();
                    editor.putInt("friend", response.body().users.size());
                    editor.commit();

                }
            }

            @Override
            public void onFailure(Call<FindUserData> call, Throwable t) {
            }

        });

    }


}

