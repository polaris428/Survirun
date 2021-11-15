package com.example.survirun.Fragmnet.Friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivityFriendInformationBinding;


public class FriendInformationActivity extends Activity {
    private ActivityFriendInformationBinding binding;
    String userName;
    String userEmile;
    String userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendInformationBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());
        Intent getIntent = getIntent();
        userName=getIntent.getStringExtra("userName");
        userEmile=getIntent.getStringExtra("userEmail");
        userProfile=getIntent.getStringExtra("profile");
        Log.d("프로필",userProfile);
        binding.usernameTextview.setText(userName);
        binding.userEmailTextView.setText(userEmile);
        Glide.with(this).load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id="+userProfile)
                .error(R.drawable.userdefaultprofile)
                .circleCrop()
                .into(binding.userProfileImageView);


    }
}