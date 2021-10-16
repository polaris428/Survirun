package com.example.survirun.activity.account;

import static com.example.survirun.R.string.email_enter;
import static com.example.survirun.R.string.email_error;
import static com.example.survirun.R.string.pwd_enter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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

import java.io.IOException;
import java.util.regex.Pattern;

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() && !s.toString().replace(" ", "").isEmpty()) {
                    binding.layout1.setErrorEnabled(false);
                    emailTrue = true;
                } else {
                    binding.layout1.setErrorEnabled(true);
                    binding.layout1.setError(getString(email_error));
                    emailTrue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.duplicateCheck.setOnClickListener(v -> {
            email = binding.idInputEdittext.getText().toString().trim();
            if (email.replace(" ", "").isEmpty()) {
                binding.layout1.setErrorEnabled(true);
                binding.layout1.setError(getString(email_enter));
            } else {
                if (emailTrue) {
                    Call<EmileCheck> call = ServerClient.getServerService().getEmileCheck(email);
                    call.enqueue(new Callback<EmileCheck>() {
                        @Override
                        public void onResponse(Call<EmileCheck> call, Response<EmileCheck> response) {
                            if (response.isSuccessful()) {
                                Log.d("adsf", response.body() + "");
                                if (response.body().exists) {
                                    Log.d("qwer", response.body().exists + "");
                                    binding.layout1.setErrorEnabled(true);
                                    Log.d("emile", email);
                                    binding.layout1.setError(getString(R.string.email_already));
                                } else {
                                    Log.d("qwer", response.body().exists + "");
                                    binding.layout1.setHelperTextEnabled(true);
                                    binding.layout1.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                                    binding.layout1.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.green)));
                                    emileCheck = true;
                                    Log.d("emile", email);
                                    binding.layout1.setHelperText(getString(R.string.email_can_use));
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
        });

        binding.passwordInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,15}$", s)) {
                    binding.layout2.setErrorEnabled(true);
                    binding.layout2.setError(getString(pwd_enter));

                } else {
                    binding.layout2.setErrorEnabled(false);

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
                if(p.replace(" ", "").isEmpty()&&!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,15}$", p)){
                    binding.layout3.setErrorEnabled(true);
                    binding.layout3.setError(getString(R.string.pwd_condition));
                }
                else{
                    binding.layout3.setErrorEnabled(false);
                    String p1 = binding.passwordCheckEdittext.getText().toString();
                    if (p1.equals(p)) {
                        binding.layout3.setErrorEnabled(false);
                        pawTrue = true;
                        Log.d("문자", p + p1);
                    } else {
                        binding.layout3.setErrorEnabled(true);
                        binding.layout3.setError(getString(R.string.pw_error));
                        pawTrue = false;
                    }
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