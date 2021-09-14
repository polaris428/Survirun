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
    FirebaseAuth firebaseAuth;

    boolean emailTrue = false;
    boolean pawTrue = false;
    String email;
    String pwe;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();


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

            if (pawTrue && emailTrue) {
                email = binding.idInputEdittext.getText().toString().trim();
                pwe = binding.passwordInputEdittext.getText().toString().trim();
                NewUserData newUserData=new NewUserData(email,pwe,"");
                Call<TokenData> call = ServerClient.getServerService().signUp(newUserData);
                call.enqueue(new Callback<TokenData>() {
                    @Override
                    public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                        if(response.isSuccessful()){


                            login();
                            LoginData loginData =new LoginData(email,pwe);
                            Call<TokenData> call1=ServerClient.getServerService().login(loginData);
                            call1.enqueue(new Callback<TokenData>() {
                                @Override
                                public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                                    if(response.isSuccessful()){
                                        Intent intent=new Intent(SignUpActivity.this,SignUpNameActivity.class);
                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onFailure(Call<TokenData> call, Throwable t) {

                                }
                            });
                        }else{
                            try {

                                Log.e("SignUpActivity",response.errorBody().string());

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