package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.survirun.Fragmnet.inFragment.Fragment2;
import com.example.survirun.R;
import com.example.survirun.data.ImageData;
import com.example.survirun.databinding.ActivityNavHeaderBinding;
import com.example.survirun.databinding.FragmentHomeBinding;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {



    FragmentHomeBinding binding;
    ActivityNavHeaderBinding nbinding;
    SharedPreferences sf;
    String token;
    String name;
    String emile;
    SharedPreferences goal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        UserFragment userFragment= new UserFragment();
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        nbinding= ActivityNavHeaderBinding.bind(binding.navView.getHeaderView(0));
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);


        token = sf.getString("token", "");

        name=sf.getString("name","");
        emile=sf.getString("email","");
        binding.userPageButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), UserPageActivity.class);
//            startActivity(intent);

            binding.drawerLayout.openDrawer(binding.navView);

        });

        nbinding.nameTextView.setText(name);
        nbinding.idTextView.setText(emile);

        Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
        getProfile.enqueue(new Callback<ImageData>() {
            @Override
            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null) {
                        return;
                    }
                    Glide.with(HomeFragment.this)
                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                            .circleCrop()
                            .error(R.drawable.ic_profile)
                            .into(nbinding.profileImageview);
                }
            }

            @Override
            public void onFailure(Call<ImageData> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return view;
    }
}