package com.example.survirun.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.BottomSheetSignUpFragment;
import com.example.survirun.R;
import com.example.survirun.activity.account.ProgressDialog;
import com.example.survirun.activity.account.SignUpProfileActivity;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivityEditProfileBinding;
import com.example.survirun.fragmnet.SettingFragment;
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

public class EditProfileActivity extends AppCompatActivity implements BottomSheetSignUpFragment.BottomSheetListener {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    ActivityEditProfileBinding binding;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    String token;
    String name;
    String inputName, inputIntro;
    String intro;
    boolean isCheck = false;

    Uri selectedImageUri;
    BottomSheetSignUpFragment signUpFragment;
    ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        signUpFragment = new BottomSheetSignUpFragment();
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customProgressDialog.setCancelable(false);

        sf = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sf.edit();

        token = sf.getString("token", "");
        name = sf.getString("name", "");
        intro = sf.getString("intro", "");

        Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
        getProfile.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                if (response.isSuccessful()) {
                    Log.d("?????????", response.body().img);
                    Glide.with(EditProfileActivity.this)
                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                            .circleCrop()
                            .placeholder(R.drawable.ic_userprofile)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.ic_userprofile)
                            .into(binding.profileImageview);
                }
            }

            @Override
            public void onFailure(Call<ImageData> call, Throwable t) {
                Glide.with(EditProfileActivity.this)
                        .load(R.drawable.ic_userprofile)
                        .circleCrop()
                        .error(R.drawable.ic_userprofile)
                        .into(binding.profileImageview);
                t.printStackTrace();
            }
        });


        binding.nameInputEdittext.setText(name);
        if (intro.equals("")) {
            binding.commentInputEdittext.setHint(R.string.introduce);
        } else {
            binding.commentInputEdittext.setText(intro);
        }

        binding.profileImageview.setOnClickListener(v -> {
            signUpFragment.show(getSupportFragmentManager(), "bottomSheet");
        });

        binding.basicProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResultData> call = ServerClient.getServerService().postDefaultImage(token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            Glide.with(EditProfileActivity.this)
                                    .load(R.drawable.ic_userprofile)
                                    .circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(binding.profileImageview);
                            Toast.makeText(EditProfileActivity.this, R.string.success_reflected, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.saveButton.setOnClickListener(v -> {
            inputName = binding.nameInputEdittext.getText().toString();
            if (!name.equals(inputName)) {
                if (inputName.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.type_name, Toast.LENGTH_SHORT).show();
                } else {
                    customProgressDialog.show();
                    isCheck = true;
                    Call<ResultData> call = ServerClient.getServerService().inputName(inputName, token);
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if (response.isSuccessful()) {
                                editor.putString("name", inputName);
                                editor.commit();
                                Toast.makeText(EditProfileActivity.this, R.string.success_reflected, Toast.LENGTH_LONG).show();
                                customProgressDialog.dismiss();
                                finish();
                            } else {
                                response.errorBody();
                                Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                                customProgressDialog.dismiss();
                                Log.d("adsf", response.errorBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultData> call, Throwable t) {
                            Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                            t.printStackTrace();
                        }
                    });

                }
            }
            if (!intro.equals(binding.commentInputEdittext.getText().toString())) {
                inputIntro = binding.commentInputEdittext.getText().toString();
                customProgressDialog.show();
                isCheck = true;
                Call<ResultData> call = ServerClient.getServerService().patchEditIntro(token, inputIntro);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            editor.putString("intro", inputIntro);
                            editor.commit();
                            Toast.makeText(EditProfileActivity.this, R.string.success_reflected, Toast.LENGTH_LONG).show();
                            customProgressDialog.dismiss();
                            finish();
                        } else {
                            customProgressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                        customProgressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                });

            }
            if (selectedImageUri != null) {
                customProgressDialog.show();
                isCheck = true;
                MultipartBody.Part body1 = prepareFilePart("image", selectedImageUri);
                Call<ResultData> call = ServerClient.getServerService().postProfile(token, body1);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, R.string.success_reflected, Toast.LENGTH_LONG).show();
                            customProgressDialog.dismiss();
                            finish();


                        } else {
                            customProgressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                            Log.d("adsf", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        customProgressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
            }
            if (!isCheck) {
                finish();
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
                    Toast.makeText(EditProfileActivity.this, R.string.permission_error_again, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.permission_error_allow_setting, Toast.LENGTH_LONG).show();
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
}