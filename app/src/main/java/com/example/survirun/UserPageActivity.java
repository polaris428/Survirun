package com.example.survirun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.survirun.activity.account.LoginActivity;
import com.example.survirun.databinding.ActivityUserPageBinding;
import com.google.firebase.auth.FirebaseAuth;

public class UserPageActivity extends AppCompatActivity {
    ActivityUserPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AlertDialog.Builder builder = new AlertDialog.Builder(UserPageActivity.this);

        binding.logoutButton.setOnClickListener(v -> {
            builder.setTitle("로그아웃").setMessage("로그아웃을 하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("아니요", null).show();
        });
    }
}