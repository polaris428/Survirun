package com.example.survirun.activity.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.FindUserData;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.ActivitySignInBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    ProgressDialog customProgressDialog;

    String email;
    String id;
    String pwe;
    String name;
    String token;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        editor = sf.edit();
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        name = sf.getString("name", "");

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        binding.idEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id = binding.idEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackgroundColor(ContextCompat.getColor(SignInActivity.this, R.color.red));
                } else {
                    binding.loginButton.setBackgroundColor(ContextCompat.getColor(SignInActivity.this, R.color.gray));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwe = binding.passwordEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackgroundColor(ContextCompat.getColor(SignInActivity.this, R.color.red));
                } else {
                    binding.loginButton.setBackgroundColor(ContextCompat.getColor(SignInActivity.this, R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.loginButton.setOnClickListener(v -> {
            if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                email = binding.idEdittext.getText().toString().trim();
                if (email.contains("@")) {
                    customProgressDialog.show();
                    customProgressDialog.setCancelable(false);
                    pwe = binding.passwordEdittext.getText().toString().trim();
                    LoginData loginData = new LoginData(email, pwe);
                    Call<TokenData> call = ServerClient.getServerService().login(loginData);
                    call.enqueue(new Callback<TokenData>() {
                        @Override
                        public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                            if (response.isSuccessful()) {
                                token=response.body().token;
                                editor.putString("email", email);
                                editor.putString("pwe", pwe);
                                editor.putString("token",token );
                                getFriendNumber();
                                editor.commit();

                                UserAccount userAccount = new UserAccount();
                                userAccount.yesterdayExercise(response.body().token, SignInActivity.this);
                                userAccount.getExercise(response.body().token, SignInActivity.this);
                                if (!response.body().username) {
                                    customProgressDialog.dismiss();

                                    Intent intent = new Intent(SignInActivity.this, SignUpNameActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    if (!response.body().profile) {
                                        customProgressDialog.dismiss();
                                        Intent intent = new Intent(SignInActivity.this, SignUpProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    } else {

                                        Call<getUserData> call1 = ServerClient.getServerService().getUser(response.body().token, email);
                                        call1.enqueue(new Callback<getUserData>() {
                                            @Override
                                            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                                                if (response.isSuccessful()) {
                                                    customProgressDialog.dismiss();
                                                    editor.putString("name", response.body().username);
                                                    editor.putString("intro", response.body().intro);
                                                    editor.putInt("score", response.body().score);
                                                    editor.commit();

                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                } else {
                                                    Log.d("adsf", "실패");
                                                    Toast.makeText(SignInActivity.this, R.string.error_server, Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<getUserData> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(SignInActivity.this, R.string.error_server, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }


                            } else {
                                customProgressDialog.dismiss();
                                binding.loginErrorMessage.setVisibility(View.VISIBLE);
                                binding.loginErrorMessage.setText(R.string.check_id_pwd);
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenData> call, Throwable t) {
                            customProgressDialog.dismiss();
                            binding.loginErrorMessage.setVisibility(View.VISIBLE);
                            binding.loginErrorMessage.setText(R.string.server_error);
                        }
                    });

                } else {

                    binding.loginErrorMessage.setVisibility(View.VISIBLE);
                    binding.loginErrorMessage.setText(R.string.email_error);
                }
            } else {

                binding.loginErrorMessage.setVisibility(View.VISIBLE);
                binding.loginErrorMessage.setText(R.string.fill_error);
            }

        });

        binding.signUpTextView1.setOnClickListener(v -> {
            setIntentSignUp();
        });
        binding.signUpTextView2.setOnClickListener(v -> {
            setIntentSignUp();
        });
    }

    private void setIntentSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
    public void getFriendNumber(){
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
