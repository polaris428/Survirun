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
import com.example.survirun.databinding.ActivitySignUpNameBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpNameActivity extends AppCompatActivity {
    ActivitySignUpNameBinding binding;
    String story;
    String name;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();


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
            String uid = FirebaseAuth.getInstance().getUid();
            Log.d("adsf", uid);
            if (name.replace(" ", "").length()==0){
               binding.nameErrMessage.setVisibility(View.VISIBLE);
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("UserProfile").child(uid).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(SignUpNameActivity.this, SignUpProfileActivity.class);
                        startActivity(intent);
                        editor.putString("name", name);
                        editor.commit();
                        finish();
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