package com.example.survirun.Fragmnet.Friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityFriendInformationBinding;

public class FriendInformationActivity extends Activity {
    private ActivityFriendInformationBinding binding;
    String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendInformationBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        result = intent.getStringExtra("list");
        binding.text.setText(result);
    }
}