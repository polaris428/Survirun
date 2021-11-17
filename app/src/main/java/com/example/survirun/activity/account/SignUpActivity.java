package com.example.survirun.activity.account;

import static com.example.survirun.R.string.check_email_validity;
import static com.example.survirun.R.string.email_already;
import static com.example.survirun.R.string.email_can_use;
import static com.example.survirun.R.string.email_enter;
import static com.example.survirun.R.string.email_error;
import static com.example.survirun.R.string.fill_condition;
import static com.example.survirun.R.string.fill_pwd_condition;
import static com.example.survirun.R.string.format_error;
import static com.example.survirun.R.string.pwd_error;
import static com.example.survirun.R.string.pwd_condition;
import static com.example.survirun.R.string.pwd_enter;
import static com.example.survirun.R.string.pwd_include;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
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
    ProgressDialog customProgressDialog;

    boolean isEmailEnterCheck = false;
    boolean isPwdCheck = false;
    boolean isEmailCheck = false;
    boolean isPwdEnter = false;
    String email;
    String pwe;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        editor = sf.edit();

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        binding.idInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() && !s.toString().replace(" ", "").isEmpty()) {
                    binding.layout1.setErrorEnabled(false);
                    isEmailEnterCheck = true;
                    setCheck();
                } else {
                    binding.layout1.setErrorEnabled(true);
                    binding.layout1.setError(getString(email_error));
                    isEmailEnterCheck = false;
                    binding.signUpButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = binding.idInputEdittext.getText().toString().trim();
                if (email.replace(" ", "").isEmpty()) {
                    binding.layout1.setErrorEnabled(true);
                    binding.layout1.setError(getString(email_enter));
                } else {
                    if (isEmailEnterCheck) {
                        Call<EmileCheck> call = ServerClient.getServerService().getEmileCheck(email);
                        call.enqueue(new Callback<EmileCheck>() {
                            @Override
                            public void onResponse(Call<EmileCheck> call, Response<EmileCheck> response) {
                                if (response.isSuccessful()) {
                                    Log.d("adsf", response.body() + "");
                                    if (response.body().exists) {
                                        binding.layout1.setErrorEnabled(true);
                                        binding.layout1.setError(getString(email_already));
                                    } else {
                                        binding.layout1.setHelperTextEnabled(true);
                                        binding.layout1.setEndIconDrawable(R.drawable.ic_check);
                                        binding.layout1.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.green)));
                                        isEmailCheck = true;
                                        binding.layout1.setHelperText(getString(email_can_use));
                                        setCheck();
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

        binding.passwordInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCheck();
                if (!Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).*$", s)) {
                    binding.layout2.setErrorEnabled(true);
                    binding.layout2.setCounterEnabled(false);
                    binding.layout2.setError(getString(pwd_include));
                    isPwdEnter = false;
                    binding.signUpButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                } else if (!Pattern.matches("^.{8,15}$", s)) {
                    binding.layout2.setErrorEnabled(true);
                    binding.layout2.setCounterEnabled(true);
                    binding.layout2.setError(getString(pwd_enter));
                    isPwdEnter = false;
                    binding.signUpButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                } else {
                    binding.layout2.setErrorEnabled(false);
                    binding.layout2.setCounterEnabled(false);
                    isPwdEnter = true;
                    setCheck();
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
                setCheck();
                String p = binding.passwordInputEdittext.getText().toString();
                if (!isPwdEnter) {
                    binding.layout3.setErrorEnabled(true);
                    binding.layout3.setError(getString(pwd_condition));
                } else {
                    binding.layout3.setErrorEnabled(false);
                    String p1 = binding.passwordCheckEdittext.getText().toString();
                    if (p1.equals(p)) {
                        binding.layout3.setErrorEnabled(false);
                        isPwdCheck = true;
                        setCheck();
                    } else {
                        binding.layout3.setErrorEnabled(true);
                        binding.layout3.setError(getString(pwd_error));
                        isPwdCheck = false;
                        binding.signUpButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.signUpButton.setOnClickListener(v -> {
            if (isEmailCheck && isEmailEnterCheck && isPwdCheck && isPwdEnter) {
                customProgressDialog.show();
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
                                        customProgressDialog.dismiss();
                                        editor.putString("token", response.body().token);
                                        editor.commit();
                                        Log.d("token", response.body().token);
                                        Intent intent = new Intent(SignUpActivity.this, SignUpNameActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<TokenData> call, Throwable t) {
                                    customProgressDialog.dismiss();
                                }
                            });
                        } else {
                            customProgressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, R.string.error_server, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenData> call, Throwable t) {
                        customProgressDialog.dismiss();
                        t.printStackTrace();
                        Toast.makeText(SignUpActivity.this, R.string.format_error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (!isEmailEnterCheck && !isPwdCheck) {
                Toast.makeText(getApplicationContext(), fill_condition, Toast.LENGTH_SHORT).show();
            } else if (!isEmailCheck) {
                Toast.makeText(getApplicationContext(), check_email_validity, Toast.LENGTH_SHORT).show();
            } else if (!isEmailEnterCheck) {
                Toast.makeText(getApplicationContext(), email_enter, Toast.LENGTH_SHORT).show();
            } else if (!isPwdCheck || !isPwdEnter) {
                Toast.makeText(getApplicationContext(), fill_pwd_condition, Toast.LENGTH_SHORT).show();
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

    private void setCheck() {
        if (isEmailCheck && isEmailEnterCheck && isPwdEnter && isPwdCheck) {
            binding.signUpButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
    }
}