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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.survirun.BottomSheetSignUpFragment;
import com.example.survirun.R;
import com.example.survirun.activity.account.SignUpProfileActivity;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.ActivityEditProfileBinding;
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

public class EditProfileActivity extends AppCompatActivity implements BottomSheetSignUpFragment.BottomSheetListener{
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    ActivityEditProfileBinding binding;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    String token;
    String name;
    String inputName;
    String intro;

    Uri selectedImageUri;
    BottomSheetSignUpFragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        signUpFragment = new BottomSheetSignUpFragment();

        sf = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sf.edit();

        token = sf.getString("token", "");
        name = sf.getString("name", "");
        intro=sf.getString("intro","");


        binding.nameInputEdittext.setText(name);
        if(intro.equals("")){
            binding.commentInputEdittext.setHint("본인을 소개해보세요");
        }else{
            binding.commentInputEdittext.setText(intro);
        }

        binding.profileImageview.setOnClickListener(v -> {
            signUpFragment.show(getSupportFragmentManager(), "bottomSheet");
        });

        binding.basicProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResultData>call=ServerClient.getServerService().postDefaultImage(token);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(EditProfileActivity.this,"성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.saveButton.setOnClickListener(v -> {
            inputName = binding.nameInputEdittext.getText().toString();
            if (!name.equals(inputName)) {
                    Call<ResultData> call = ServerClient.getServerService().inputName(inputName, token);
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if (response.isSuccessful()) {
                                editor.putString("name", inputName);
                                editor.commit();
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            } else {
                                response.errorBody();
                                Log.d("adsf", response.errorBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultData> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });


            }
            if(!intro.equals(binding.commentInputEdittext.getText().toString())){

                    Call<ResultData>call =ServerClient.getServerService().patchEditIntro(token,binding.commentInputEdittext.getText().toString());
                    call.enqueue(new Callback<ResultData>() {
                        @Override
                        public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this,"성공적으로 반영되었습니다",Toast.LENGTH_LONG).show();
                                editor.putString("intro",binding.commentInputEdittext.getText().toString());
                                editor.commit();


                            }else{
                                Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultData> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, R.string.server_error,Toast.LENGTH_LONG).show();
                        }
                    });

            }
        });

    }

    @Override
    public void onClickGallery() {//저장소 읽어오는 퍼미션
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }//퍼미션이 허용되지 않아 있다면 퍼미션 허용메세지 생성 & 코드0보내기
        else {
            setGallery();//퍼미션이 허용되어 있다면 갤러리불러오기
        }
        signUpFragment.dismiss();
    }

    @Override
    public void onClickCamera() {//카메라 퍼미션
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);//코드 1보내기
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
        //퍼미션허용 메세지를 보냈을떄
        if (Build.VERSION.SDK_INT >= 23) {

            // requestPermission의 배열의 index가 아래 grantResults index와 매칭
            // 퍼미션이 승인되면
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 0) {//위에서 코드 0을 보냄
                    setGallery();
                } else if (requestCode == 1) {//코드 1
                    setCamera();
                }

            }
            // 퍼미션이 승인 거부되면
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    //퍼미션 2번까진 아래코드
                    Toast.makeText(EditProfileActivity.this, R.string.permission_error_again, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, R.string.permission_error_allow_setting, Toast.LENGTH_LONG).show();
                    //2번이후론 알아서
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {//코드 0번 갤러리
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    selectedImageUri = getImageUri(this, img);
                    Glide.with(getApplicationContext()).load(img).into(binding.profileImageview);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 1) {//코드 1번 카메라
            if (resultCode == RESULT_OK) {
                try {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImageUri = getImageUri(this, imageBitmap);
                    Glide.with(getApplicationContext()).load(imageBitmap).into(binding.profileImageview);
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

    public Uri getImageUri(Context ctx, Bitmap bitmap) {//비트맵 Uri로 변환
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