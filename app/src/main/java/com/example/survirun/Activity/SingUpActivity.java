package com.example.survirun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.UserModel;
import com.example.survirun.databinding.ActivitySingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {
    ActivitySingUpBinding binding;
    String id;
    FirebaseAuth firebaseAuth;

    boolean emailtrue = false;
    boolean pawtrue = false;
    Uri selectedImageUri;
    String email;
    String pwe;
    private final int GET_GALLERY_IMAGE = 200;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseAuth = FirebaseAuth.getInstance();


        binding.idinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emile = binding.idinput.getText().toString();
                if (emile.indexOf("@") != -1) {
                    binding.idtext.setVisibility(View.INVISIBLE);
                    emailtrue = true;
                } else {
                    binding.idtext.setVisibility(View.VISIBLE);
                    emailtrue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.pawinput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String p = binding.pawinput1.getText().toString();
                String p1 = binding.pawinput2.getText().toString();
                if (p.equals(p1)) {
                    binding.pwetext.setVisibility(View.INVISIBLE);
                    pawtrue = true;
                    Log.d("문자", p + p1);
                } else {
                    binding.pwetext.setVisibility(View.VISIBLE);
                    pawtrue = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.joinbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (pawtrue && emailtrue ) {
                    email = binding.idinput.getText().toString().trim();
                    pwe = binding.pawinput1.getText().toString().trim();


                    firebaseAuth.createUserWithEmailAndPassword(email, pwe)
                            .addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        int idx = email.indexOf("@");
                                        id = email.substring(0, idx);
                                        Log.d("adsaf", " " + email + id + pwe + SingUpActivity.this);

                                        login();
                                        String uid = task.getResult().getUser().getUid();



                                                UserModel userModel = new UserModel();
                                                userModel.id=id;

                                                userModel.uid=uid;
                                                userModel.todayExerciseTime=0;
                                                userModel.todayKm=0;
                                                userModel.todayCalorie=0;

                                                FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            } else {
                                        Toast.makeText(SingUpActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            });

                } else {
                    Toast.makeText(SingUpActivity.this, "형식을 확인해주세요", Toast.LENGTH_SHORT).show();
                }
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