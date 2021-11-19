package com.example.survirun.activity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
    SharedPreferences.Editor editor;

    ProgressDialog customProgressDialog;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        token = sf.getString("token", "");
        editor = sf.edit();


        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        binding.textView.setCharacterDelay(90);
        binding.textView.displayTextWithAnimation(getText(R.string.story_name));

        story = (String) getText(R.string.story_name);
        binding.nameInputEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().replace(" ", "").isEmpty()) {
                    binding.inputLayout.setErrorEnabled(false);
                    binding.nameSendButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                }
                else{
                    binding.nameSendButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.nameSendButton.setOnClickListener(v -> {
            name = binding.nameInputEdittext.getText().toString();
            if (name.replace(" ", "").isEmpty()) {
                binding.inputLayout.setErrorEnabled(true);
                binding.inputLayout.setError(getString(R.string.type_name));
            } else {
                customProgressDialog.show();
                Call<ResultData> call = ServerClient.getServerService().inputName(name, token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            editor.putString("name", name);
                            editor.commit();
                            customProgressDialog.dismiss();
                            Intent intent = new Intent(SignUpNameActivity.this, SignUpProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            response.errorBody();
                            customProgressDialog.dismiss();
                            Log.d("adsf", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        customProgressDialog.dismiss();
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
    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        dialog.findViewById(R.id.help_button).setVisibility(View.GONE);
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> finishAffinity());
    }
}