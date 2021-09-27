package com.example.survirun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.activity.account.LoginActivity;
import com.example.survirun.data.JwtToken;
import com.example.survirun.data.ProfileImageData;
import com.example.survirun.databinding.ActivityUserPageBinding;
import com.example.survirun.server.ServerClient;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.Buffer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPageActivity extends AppCompatActivity {
    ActivityUserPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        String token=sf.getString("token","");
        String name=sf.getString("name","");
        Log.d("adsf",token);
        binding.profileImageview.setImageResource(R.drawable.ic_profile);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserPageActivity.this);
        Call<JwtToken> call1=ServerClient.getServerService().getJwt(token);
        call1.enqueue(new Callback<JwtToken>() {
            @Override
            public void onResponse(Call<JwtToken> call, Response<JwtToken> response) {
                if(response.isSuccessful()){
                    Log.d("adf",response.body()._id);
                    Glide.with(UserPageActivity.this)
                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id="+response.body()._id)
                            .error(R.drawable.ic_profile)
                            .into(binding.profileImageview);


                }
            }

            @Override
            public void onFailure(Call<JwtToken> call, Throwable t) {

            }
        });

        binding.logoutButton.setOnClickListener(v -> {
            builder.setTitle(R.string.logout).setMessage(R.string.logout_user).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.no, null).show();
        });
    }
}