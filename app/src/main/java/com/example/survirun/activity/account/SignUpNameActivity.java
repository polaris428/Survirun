package com.example.survirun.activity.account;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;


import com.example.survirun.R;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivitySignUpNameBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpNameActivity extends AppCompatActivity {
    ActivitySignUpNameBinding binding;
    String story;
    String name;
    String token;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        token = sf.getString("token", "");
        binding.textView.setCharacterDelay(160);
        binding.textView.displayTextWithAnimation("안녕하세요");

        story = (String) getText(R.string.story_name);
        binding.nameInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>=1){
                    binding.nameErrMessage.setVisibility(View.INVISIBLE);
                }
                binding.nameSendButton.setBackground(getDrawable(R.drawable.rounded_btncolor));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.nameSendButton.setOnClickListener(v -> {
            name = binding.nameInputEdittext.getText().toString();
            if (name.replace(" ", "").length()==0){
               binding.nameErrMessage.setVisibility(View.VISIBLE);
            }
            else{


                Call<ResultData> call= ServerClient.getServerService().inputName(name,token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if(response.isSuccessful()){
                            sf.edit().putString("name",name);
                            sf.edit().commit();
                            Intent intent=new Intent(SignUpNameActivity.this,SignUpProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            response.errorBody();
                            Log.d("adsf",   response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

        });
        Handler animationCompleteCallBack = new Handler(msg -> {
            Log.i("Log", "Animation Completed");
            return false;
        });




    }


}