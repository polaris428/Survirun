package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.survirun.R;
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
    String inputName;
    String intro;

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
        intro=sf.getString("intro","");

        binding.nameInputEdittext.setText(name);
        if(intro.equals("")){
            binding.commentInputEdittext.setHint("본인을 소개해보세요");
        }else{
            binding.commentInputEdittext.setText(intro);
        }
        binding.profileImageview.setOnClickListener(v -> {

        });
        binding.basicProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResultData>call=ServerClient.getServerService().postDefaultImage(token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(EditProfileActivity.this,"성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.saveButton.setOnClickListener(v -> {
            inputName = binding.nameInputEdittext.getText().toString();
            if (!name.equals(inputName)) {
                    Call<ResultData> call = ServerClient.getServerService().inputName(inputName, token);
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if (response.isSuccessful()) {
                                editor.putString("name", inputName);
                                editor.commit();
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

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
            if(!intro.equals(binding.commentInputEdittext.getText().toString())){

                    Call<ResultData>call =ServerClient.getServerService().patchEditIntro(token,binding.commentInputEdittext.getText().toString());
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this,"성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
                                editor.putString("intro",binding.commentInputEdittext.getText().toString());
                                editor.commit();


                            }else{
                                Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultData> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                        }
                    });

            }
        });

    }

}