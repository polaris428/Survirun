package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.data.ImageData;
import com.example.survirun.databinding.FragmentSettingBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingFragment extends Fragment {

    FragmentSettingBinding binding;
    SharedPreferences sf;
    String token;
    String name;
    String emile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentSettingBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);


        token = sf.getString("token", "");

        name=sf.getString("name","");
        emile=sf.getString("email","");

        binding.nameTextView.setText(name);
        binding.emileText.setText(emile);

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
                            .error(R.drawable.ic_profile)
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
}