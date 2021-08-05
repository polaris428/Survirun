package com.example.survirun.Activity;

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
import com.example.survirun.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActivityLoginBinding binding;

    boolean idchak=false;
    boolean pwechak=false;
    String uid;
    String email;
    String id;
    String pwe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login",MODE_PRIVATE);
        email = sf.getString("email","");
        pwe = sf.getString("pwe","");

        binding.idEdiText.setText(email);
        binding.pawEditText.setText(pwe);
        if(email!=null&&pwe!=null){
            idchak=true;
            pwechak=true;
        }
        binding.idEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String id=binding.idEdiText.getText().toString();
                idchak=true;
                if (pwechak&&idchak){
                    //binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String id=binding.idEdiText.getText().toString();
                idchak=true;
                if (pwechak&&idchak){
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.pawEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pwechak=true;
                if (pwechak&&idchak){
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String id=binding.idEdiText.getText().toString();
                idchak=true;
                if (pwechak&&idchak){
                    binding.loginButton.setBackground(getDrawable(R.drawable.rounded_btncolor));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firebaseAuth = firebaseAuth.getInstance();//firebaseAuth의 인스턴스를 가져옴
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwechak==true&&idchak==true){
                     email = binding.idEdiText.getText().toString().trim();

                    if(email.contains("@")==true){
                        int idx = email.indexOf("@");
                         id = email.substring(0, idx);
                         pwe = binding.pawEditText.getText().toString().trim();
                        firebaseAuth.signInWithEmailAndPassword(email, pwe)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {//성공했을때
                                            login();
                                            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }else {
                        Toast.makeText(LoginActivity.this, "이메일 형식이 맞지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "빈 칸을 전부 채워주세요", Toast.LENGTH_SHORT).show();
                }

            }


        });

        binding.signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
            }
        });

    }
    public void login(){
            SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
            SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
            editor.putString("id", id);
            editor.putString("pwe", pwe);
            editor.putString("email", email);

            editor.commit();




    }




}
