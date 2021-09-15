package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.databinding.ActivitySignUpProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignUpProfileActivity extends AppCompatActivity {
    String name;
    String token;
    ActivitySignUpProfileBinding binding;
    private final int GET_GALLERY_IMAGE = 200;
    Uri selectedImageUri;



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.profileImageview.setBackground(new ShapeDrawable(new OvalShape()));
        binding.profileImageview.setClipToOutline(true);
        binding.textView.setCharacterDelay(160);
        binding.textView.displayTextWithAnimation("안녕하세요");
        String story= name+(String) getText(R.string.story_profile);
        Handler animationCompleteCallBack = new Handler(msg -> {
            Log.i("Log", "Animation Completed");
            return false;
        });

        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);    // test 이름의 기본모드 설정
        token = sf.getString("token", "");
        name=sf.getString("name","");
        binding.profileImageview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, GET_GALLERY_IMAGE);
        });

        binding.nextButton.setOnClickListener(v -> {
            if(selectedImageUri!=null) {
                FirebaseStorage.getInstance().getReference().child("userImages").child(token).putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Intent intent = new Intent(SignUpProfileActivity.this, ResultActivity.class);
                        startActivity(intent);
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.profile_error, Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(getApplicationContext()).load(selectedImageUri).into(binding.profileImageview);
        }
    }
}