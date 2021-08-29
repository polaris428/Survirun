package com.example.survirun.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.example.survirun.Typewriter;
import com.example.survirun.databinding.ActivitySingUpProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SingUpProfileActivity extends AppCompatActivity {
    String name="name";
    ActivitySingUpProfileBinding binding;
    private final int GET_GALLERY_IMAGE = 200;
    Uri selectedImageUri;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySingUpProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding.profileImageView.setBackground(new ShapeDrawable(new OvalShape()));
        binding.profileImageView.setClipToOutline(true);

        String story= name+(String) getText(R.string.story_profile);
        Handler animationCompleteCallBack = new Handler(msg -> {
            Log.i("Log", "Animation Completed");
            return false;
        });


        binding.profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, GET_GALLERY_IMAGE);
        });

        binding.nextButton.setOnClickListener(v -> {
            if(selectedImageUri!=null) {
                FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Intent intent = new Intent(SingUpProfileActivity.this, ResultActivity.class);
                        startActivity(intent);
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.profile_error, Toast.LENGTH_SHORT).show();
            }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(getApplicationContext()).load(selectedImageUri).into(binding.profileImageView);
        }
    }
}