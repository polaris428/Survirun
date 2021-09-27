package com.example.survirun.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.data.EmileCheck;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.NewUserData;
import com.example.survirun.data.TokenData;
import com.example.survirun.databinding.ActivitySignUpBinding;
import com.example.survirun.server.ServerClient;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    String id;

    boolean emailTrue = false;
    boolean pawTrue = false;
    boolean emileCheck = false;
    String email;
    String pwe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.idInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emile = binding.idInputEdittext.getText().toString();
                if (emile.indexOf("@") != -1) {
                    binding.idErrorTextview.setVisibility(View.INVISIBLE);
                    emailTrue = true;
                } else {
                    binding.idErrorTextview.setVisibility(View.VISIBLE);
                    emailTrue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.duplicateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailTrue) {
                    email = binding.idInputEdittext.getText().toString().trim();
                    if (email.equals("")) {
                        binding.idErrorTextview.setVisibility(View.VISIBLE);
                        binding.idErrorTextview.setText(R.string.email_enter);
                    } else {
                        email = binding.idInputEdittext.getText().toString().trim();
                        Call<EmileCheck> call = ServerClient.getServerService().getEmileCheck(email);
                        call.enqueue(new Callback<EmileCheck>() {
                            @Override
                            public void onResponse(Call<EmileCheck> call, Response<EmileCheck> response) {
                                if (response.isSuccessful()) {
                                    Log.d("adsf", response.body() + "");
                                    if (response.body().exists) {
                                        Log.d("qwer", response.body().exists + "");
                                        binding.idErrorTextview.setVisibility(View.VISIBLE);
                                        Log.d("emile", email);
                                        binding.idErrorTextview.setText(R.string.email_already);
                                    } else {
                                        Log.d("qwer", response.body().exists + "");
                                        binding.idErrorTextview.setVisibility(View.VISIBLE);
                                        emileCheck = true;
                                        Log.d("emile", email);
                                        binding.idErrorTextview.setText(R.string.email_can_use);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<EmileCheck> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }
            }
        });
        binding.passwordCheckEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String p = binding.passwordInputEdittext.getText().toString();
                String p1 = binding.passwordCheckEdittext.getText().toString();
                if (p.equals(p1)) {
                    binding.passwordErrorTextview.setVisibility(View.INVISIBLE);
                    pawTrue = true;
                    Log.d("문자", p + p1);
                } else {
                    binding.passwordErrorTextview.setVisibility(View.VISIBLE);
                    pawTrue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.signUpButton.setOnClickListener(v -> {
            if (pawTrue && emailTrue && emileCheck) {
                pwe = binding.passwordInputEdittext.getText().toString().trim();
                NewUserData newUserData = new NewUserData(email, pwe, "");
                Call<TokenData> call = ServerClient.getServerService().signUp(newUserData);
                call.enqueue(new Callback<TokenData>() {
                    @Override
                    public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                        if (response.isSuccessful()) {
                            login();
                            LoginData loginData = new LoginData(email, pwe);
                            Call<TokenData> call1 = ServerClient.getServerService().login(loginData);
                            call1.enqueue(new Callback<TokenData>() {
                                @Override
                                public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                                    if (response.isSuccessful()) {
                                        Intent intent = new Intent(SignUpActivity.this, SignUpNameActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<TokenData> call, Throwable t) {

                                }
                            });
                        } else {
                            try {

                                Log.e("SignUpActivity", response.errorBody().string());

                            } catch (IOException e) {
                                Toast.makeText(SignUpActivity.this, R.string.format_error, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenData> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(SignUpActivity.this, R.string.format_error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void login() {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        editor.putString("id", id);
        editor.putString("pwe", pwe);
        editor.putString("email", email);
        editor.commit();
    }
}