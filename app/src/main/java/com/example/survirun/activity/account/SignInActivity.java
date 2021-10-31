package com.example.survirun.activity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.ActivitySignInBinding;
import com.example.survirun.server.ServerClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    ProgressDialog customProgressDialog;

    String email;
    String id;
    String pwe;
    String name;
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
                id = binding.idEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id = binding.idEdittext.getText().toString();
                pwe = binding.passwordEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.rounded_btn));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.passwordEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pwe = binding.passwordEdittext.getText().toString();
                id = binding.idEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwe = binding.passwordEdittext.getText().toString();
                if (!pwe.replace(" ", "").isEmpty() && !id.replace(" ", "").isEmpty()) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
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
                    pwe = binding.passwordEdittext.getText().toString().trim();
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

                                        Call<getUserData>call1=ServerClient.getServerService().getUser(response.body().token,email);
                                        call1.enqueue(new Callback<getUserData>() {
                                            @Override
                                            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                                                if(response.isSuccessful()){
                                                    customProgressDialog.dismiss();
                                                    editor.putString("name",response.body().username);
                                                    editor.commit();

                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }else{
                                                    Log.d("adsf","실패");
                                                    Toast.makeText(SignInActivity.this,"사버오류 잠시후 다시 실행해주세요",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<getUserData> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(SignInActivity.this,"사버오류 잠시후 다시 실행해주세요",Toast.LENGTH_SHORT).show();
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
    private void setIntentSignUp(){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
