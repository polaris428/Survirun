package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.LoginData;
import com.example.survirun.data.TokenData;
import com.example.survirun.databinding.ActivityLoginBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    String email;
    String id;
    String pwe;
    String name;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        editor = sf.edit();
        email = sf.getString("email", "");
        pwe = sf.getString("pwe", "");
        name=sf.getString("name","");


        binding.idEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                id = binding.idEdittext.getText().toString();

                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id = binding.idEdittext.getText().toString();
                pwe = binding.passwordEdittext.getText().toString();
                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
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
                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                } else {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pwe = binding.passwordEdittext.getText().toString();

                if (!pwe.equals("") && !id.equals("")) {
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));

                } else {

                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btn));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //firebaseAuth의 인스턴스를 가져옴
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwe.equals("") && !id.equals("")) {
                    email = binding.idEdittext.getText().toString().trim();

                    if (email.contains("@") == true) {
                        pwe = binding.passwordEdittext.getText().toString().trim();
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

                                    if(!response.body().username){
                                        Intent intent=new Intent(LoginActivity.this, SignUpNameActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }else{
                                        if(!response.body().profile){
                                            Intent intent=new Intent(LoginActivity.this, SignUpProfileActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        }else{
                                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }


                                }else{
                                    binding.loginErrMessage.setVisibility(View.VISIBLE);
                                    binding.loginErrMessage.setText("아이디 또는 비밀번호를 확인해주세요");
                                }
                            }

                            @Override
                            public void onFailure(Call<TokenData> call, Throwable t) {
                                binding.loginErrMessage.setVisibility(View.VISIBLE);
                                binding.loginErrMessage.setText("서버오류 서비스 이용에 불편을 드려 죄송합니다 ");
                            }
                        });

                    } else {
                        Toast.makeText(LoginActivity.this, R.string.email_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.fill_error, Toast.LENGTH_SHORT).show();
                }

            }


        });

        binding.signUpTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }




}
