package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.survirun.activity.account.SignUpNameActivity;
import com.example.survirun.activity.account.SignUpProfileActivity;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivityEditProfileBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    String token;
    String name;
    String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sf = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sf.edit();

        token = sf.getString("token", "");
        name = sf.getString("name", "");

        binding.nameEditTextview.setText(name);

        binding.saveButton.setOnClickListener(v -> {
            name = binding.nameEditTextview.getText().toString();
            if (name.replace(" ", "").isEmpty()) {

            } else {

                Call<ResultData> call = ServerClient.getServerService().inputName(name, token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            editor.putString("name", name);
                            editor.commit();
                            Intent intent = new Intent(EditProfileActivity.this, SignUpProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            response.errorBody();
                            Log.d("adsf", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

        });

    }
}