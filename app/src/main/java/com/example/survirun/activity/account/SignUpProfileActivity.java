package com.example.survirun.activity.account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.survirun.BottomSheetSignUpFragment;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.activity.WelcomeActivity;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivitySignUpProfileBinding;
import com.example.survirun.server.ServerClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpProfileActivity extends AppCompatActivity implements BottomSheetSignUpFragment.BottomSheetListener {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    String token;
    String email;

    ActivitySignUpProfileBinding binding;
    Uri selectedImageUri;
    BottomSheetSignUpFragment signUpFragment;

    SharedPreferences sf;
    SharedPreferences.Editor editor;

    ProgressDialog customProgressDialog;
    Dialog dialog;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        signUpFragment = new BottomSheetSignUpFragment();

        sf = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sf.edit();
        SharedPreferences sharedPreferences = getSharedPreferences("checkFirstAccess", MODE_PRIVATE);
        boolean checkFirstAccess = sharedPreferences.getBoolean("checkFirstAccess", false);

        binding.textView.setCharacterDelay(90);

        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        String story = "" + getText(R.string.story_profile);
        binding.textView.displayTextWithAnimation(story);

        final Animation animTransRight = AnimationUtils.loadAnimation(
                this, R.anim.right_to_left);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.profileLayout.startAnimation(animTransRight);
        }, 200);


        token = sf.getString("token", "");

        email = sf.getString("email", "");

        binding.profileImageview.setOnClickListener(v -> {
            signUpFragment.show(getSupportFragmentManager(), "bottomSheet");
        });

        binding.nextButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                customProgressDialog.show();
                MultipartBody.Part body1 = prepareFilePart("image", selectedImageUri);
                Call<ResultData> call = ServerClient.getServerService().postProfile(token, body1);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            editor.putBoolean("profile", true);
                            customProgressDialog.dismiss();
                            if (!checkFirstAccess) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("checkFirstAccess", true);
                                editor.apply();
                                Intent tutorialIntent = new Intent(SignUpProfileActivity.this, WelcomeActivity.class);
                                startActivity(tutorialIntent);
                            } else {
                                Intent intent = new Intent(SignUpProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            customProgressDialog.dismiss();
                            Log.d("adsf", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        customProgressDialog.dismiss();
                        t.printStackTrace();
                    }
                });
            } else {
                customProgressDialog.show();
                Call<ResultData> postDefaultImage = ServerClient.getServerService().postDefaultImage(token);
                postDefaultImage.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            editor.putBoolean("profile", true);
                            customProgressDialog.dismiss();
                            Intent intent = new Intent(SignUpProfileActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            customProgressDialog.dismiss();
                            Toast.makeText(SignUpProfileActivity.this, R.string.profile_error, Toast.LENGTH_LONG).show();
                            Log.d("??????", response.errorBody().toString());
                            Log.d("adf", token);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        customProgressDialog.dismiss();
                        Toast.makeText(SignUpProfileActivity.this, R.string.profile_error, Toast.LENGTH_LONG);
                    }
                });
            }
        });


    }

    @Override
    public void onClickGallery() {//????????? ???????????? ?????????
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }//???????????? ???????????? ?????? ????????? ????????? ??????????????? ?????? & ??????0?????????
        else {
            setGallery();//???????????? ???????????? ????????? ?????????????????????
        }
        signUpFragment.dismiss();
    }

    @Override
    public void onClickCamera() {//????????? ?????????
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);//?????? 1?????????
        } else {
            setCamera();
        }
        signUpFragment.dismiss();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //??????????????? ???????????? ????????????
        if (Build.VERSION.SDK_INT >= 23) {

            // requestPermission??? ????????? index??? ?????? grantResults index??? ??????
            // ???????????? ????????????
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 0) {//????????? ?????? 0??? ??????
                    setGallery();
                } else if (requestCode == 1) {//?????? 1
                    setCamera();
                }

            }
            // ???????????? ?????? ????????????
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    //????????? 2????????? ????????????
                    Toast.makeText(SignUpProfileActivity.this, R.string.permission_error_again, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpProfileActivity.this, R.string.permission_error_allow_setting, Toast.LENGTH_LONG).show();
                    //2???????????? ?????????
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {//?????? 0??? ?????????
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    selectedImageUri = getImageUri(this, img);
                    Glide.with(getApplicationContext()).load(img).circleCrop().into(binding.profileImageview);
                } catch (Exception e) {

                }
            }
        }
        if (requestCode == 1) {//?????? 1??? ?????????
            if (resultCode == RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImageUri = getImageUri(this, imageBitmap);
                    Glide.with(getApplicationContext()).load(imageBitmap).circleCrop().into(binding.profileImageview);
                } catch (Exception e) {

                }
            }
        }
    }

    private void setGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    private void setCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public Uri getImageUri(Context ctx, Bitmap bitmap) {//????????? Uri??? ??????
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage
                (ctx.getContentResolver(),
                        bitmap, "Temp", null);
        return Uri.parse(path);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(getRealPathFromURI(fileUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    public void showDialog() {
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        dialog.findViewById(R.id.help_button).setVisibility(View.GONE);
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> {
            finishAffinity();
            dialog.dismiss();
        });
    }
}