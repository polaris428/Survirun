package com.example.survirun.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivitySignUpProfileBinding;
import com.example.survirun.server.ServerClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class SignUpProfileActivity extends AppCompatActivity {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    String name;
    String token;
    String email;
    ActivitySignUpProfileBinding binding;
    private final int GET_GALLERY_IMAGE = 200;
    Uri selectedImageUri;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences.Editor editor;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        editor=sf.edit();

        binding.profileImageview.setBackground(new ShapeDrawable(new OvalShape()));
        binding.profileImageview.setClipToOutline(true);
        binding.textView.setCharacterDelay(160);
        binding.textView.displayTextWithAnimation("안녕하세요");
        String story= name+(String) getText(R.string.story_profile);
        Handler animationCompleteCallBack = new Handler(msg -> {
            Log.i("Log", "Animation Completed");
            return false;
        });

        token = sf.getString("token", "");
        name=sf.getString("name","");
        email = sf.getString("email", "");
        binding.profileImageview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, GET_GALLERY_IMAGE);
        });

        binding.nextButton.setOnClickListener(v -> {
            if(selectedImageUri!=null) {


                MultipartBody.Part body1 = prepareFilePart("image", selectedImageUri);
                Call<ResultData>call= ServerClient.getServerService().postProfile(token,body1);

                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if(response.isSuccessful()){
                            editor.putBoolean("profile",true);
                            Intent intent =new Intent(SignUpProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Log.d("adsf",response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(getRealPathFromURI(fileUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }




}