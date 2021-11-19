package com.example.survirun.fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.activity.EditProfileActivity;
import com.example.survirun.R;
import com.example.survirun.activity.WelcomeActivity;
import com.example.survirun.activity.exercise.SplashActivity2;
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
    SharedPreferences sf, sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorBoolean;

    String token;
    String name;
    String emile;
    String intro;

    Dialog dialog;
    int score;
    int friendNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Log.d("asdf", "onCreateView");

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        sharedPreferences = getContext().getSharedPreferences("checkFirstAccess", MODE_PRIVATE);
        editorBoolean = sharedPreferences.edit();


        binding.clearCacheButton.setOnClickListener(v -> {
            setDialog(getString(R.string.delete_cache_text));
            dialog.findViewById(R.id.yes_button).setOnClickListener(v1 -> {
                deleteDir(getContext().getCacheDir());
                dialog.dismiss();
            });
        });

        binding.logoutButton.setOnClickListener(v -> {
            setDialog(getString(R.string.sign_out_user));
            dialog.findViewById(R.id.yes_button).setOnClickListener(v1 -> {
                editor.putString("email", "");
                editor.putString("pwe", "");
                editor.putString("token", "");
                editor.putString("name", "");
                editor.putString("intro", "");
                editorBoolean.putBoolean("checkFirstAccess", false);
                editorBoolean.commit();
                editor.commit();
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), SplashActivity2.class);
                startActivity(intent);
                getActivity().finish();
            });
        });

        binding.goalButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UserGoalActivity.class));
        });

        binding.editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.helpButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                startActivity(intent);
        });

        binding.bugButton.setOnClickListener(view1 -> {

            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("plain/text");
            String[] address = {"survirun@gmail.com"};
            email.putExtra(Intent.EXTRA_EMAIL, address);
            email.putExtra(Intent.EXTRA_SUBJECT, R.string.network_error);
            email.setPackage("com.google.android.gm");
            email.putExtra(Intent.EXTRA_TEXT, R.string.error_detail);
            startActivity(email);

        });


        return view;
    }

    public void setDialog(String text) {
        TextView explain = dialog.findViewById(R.id.explain_textView);
        explain.setText(text);
        dialog.show();
        dialog.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    @Override
    public void onResume() {
        super.onResume();
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sf.edit();

        token = sf.getString("token", "");
        name = sf.getString("name", "");
        emile = sf.getString("email", "");
        intro = sf.getString("intro", "");
        score = sf.getInt("score", 0);
        friendNumber = sf.getInt("friend", 0);


        if (intro.equals("")) {
            binding.infoText.setVisibility(View.GONE);
        } else {
            binding.infoText.setVisibility(View.VISIBLE);
            binding.infoText.setText(intro);
        }

        binding.nameTextView.setText(name);
        binding.emailText.setText(emile);
        binding.bestScoreTextView.setText(String.valueOf(score));
        binding.friendNumberTextView.setText(String.valueOf(friendNumber));

        Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
        getProfile.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null) {
                        return;
                    }
                    Log.d("프로필", response.body().img);
                    Glide.with(SettingFragment.this)
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
                Glide.with(SettingFragment.this)
                        .load(R.drawable.ic_userprofile)
                        .circleCrop()
                        .error(R.drawable.ic_userprofile)
                        .into(binding.profileImageview);
                t.printStackTrace();
            }
        });
    }

}