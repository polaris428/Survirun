package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.activity.account.LoginActivity;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.InfoData;
import com.example.survirun.databinding.ActivityUserPageBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPageActivity extends AppCompatActivity {
    ActivityUserPageBinding binding;

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    String token;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        name = sf.getString("name", "");
        editor = sf.edit();
        Log.d("adsf", token);
        binding.profileImageview.setBackground(new ShapeDrawable(new OvalShape()));
        binding.profileImageview.setClipToOutline(true);
        binding.profileImageview.setImageResource(R.drawable.ic_profile);
        builder = new AlertDialog.Builder(UserPageActivity.this);

        Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
        getProfile.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                if (response.isSuccessful()) {
                    Log.d("adf", response.body().img);
                    Glide.with(UserPageActivity.this)
                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                            .error(R.drawable.ic_profile)
                            .into(binding.profileImageview);
                }
            }

            @Override
            public void onFailure(Call<ImageData> call, Throwable t) {
                t.printStackTrace();
            }
        });
        Call<InfoData>call=ServerClient.getServerService().getInfo(token);
        call.enqueue(new Callback<InfoData>() {
            @Override
            public void onResponse(Call<InfoData> call, Response<InfoData> response) {
                if(response.isSuccessful()){
                    binding.emileText.setText(response.body().emile);
                    editor.putString("name", response.body().username);
                    editor.commit();
                    binding.nameTextView.setText(name);
                }
            }

            @Override
            public void onFailure(Call<InfoData> call, Throwable t) {

            }
        });
        binding.logoutButton.setOnClickListener(v -> {
            builder.setTitle(R.string.logout).setMessage(R.string.logout_user).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putString("email", "");
                    editor.putString("pwe", "");
                    editor.putString("token", "");
                    editor.putString("name", "");
                    editor.commit();
                    Intent intent = new Intent(UserPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.no, null).show();
        });
    }
}