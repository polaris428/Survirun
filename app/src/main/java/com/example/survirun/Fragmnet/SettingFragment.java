package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.EditProfileActivity;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;
import com.example.survirun.activity.WelcomeActivity;
import com.example.survirun.activity.user.UserGoalActivity;
import com.example.survirun.data.ImageData;
import com.example.survirun.databinding.FragmentSettingBinding;
import com.example.survirun.server.ServerClient;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingFragment extends Fragment {

    FragmentSettingBinding binding;
    SharedPreferences sf;

    String token;
    String name;
    String emile;

    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSettingBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        token = sf.getString("token", "");

        name=sf.getString("name","");
        emile=sf.getString("email","");

        binding.nameTextView.setText(name);
        binding.emileText.setText(emile);

        binding.clearCacheButton.setOnClickListener(v -> {
            showDialog();
        });

        binding.goalButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UserGoalActivity.class));
        });

        binding.editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

         binding.helpButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent =new Intent(getActivity(), WelcomeActivity.class);
                 startActivity(intent);
             }
         });

        Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
        getProfile.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null) {
                        return;
                    }
                    Glide.with(SettingFragment.this)
                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.ic_userprofile)
                            .into(binding.profileImageview);
                }
            }

            @Override
            public void onFailure(Call<ImageData> call, Throwable t) {
                Glide.with(SettingFragment.this)
                        .load(R.drawable.ic_profile)
                        .circleCrop()
                        .error(R.drawable.ic_profile)
                        .into(binding.profileImageview);
                t.printStackTrace();
            }
        });
        return view;
    }
    public void showDialog() {
        TextView explain = dialog.findViewById(R.id.explain_textView);
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button helpButton = dialog.findViewById(R.id.help_button);
        helpButton.setVisibility(View.GONE);
        explain.setText("캐시를 삭제하시겠습니까?");
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> {
            deleteDir(getActivity().getApplication().getCacheDir());
            dialog.dismiss();
        });
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}