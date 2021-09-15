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
import android.widget.Toast;


import com.example.survirun.R;
import com.example.survirun.Typewriter;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivitySignUpNameBinding;
import com.example.survirun.server.ServerClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Path;

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
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        token = sf.getString("token", "");
        Log.d("와아",token);


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
                Log.d("asdf",name);
                Log.d("asdf",token+"토큰");

                Call<ResultData> call= ServerClient.getServerService().inputName(name,token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if(response.isSuccessful()){
                            Intent intent=new Intent(SignUpNameActivity.this,SignUpProfileActivity.class);
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

        Typewriter typewriter = new Typewriter(this);
        typewriter.setCharacterDelay(100);
        typewriter.setTextSize(20);
        typewriter.setTextColor(R.color.black);
        typewriter.setTypeface(null, Typeface.NORMAL);
        typewriter.setPadding(20, 20, 20, 20);
        typewriter.setAnimationCompleteListener(animationCompleteCallBack);
        typewriter.animateText(story);
        setContentView(typewriter);
        Handler handler = new Handler();
        handler.postDelayed(() -> setContentView(view), 5000); //딜레이 타임 조절


    }


}