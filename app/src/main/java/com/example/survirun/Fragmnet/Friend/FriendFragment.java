package com.example.survirun.Fragmnet.Friend;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FindUserData;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.FragmentFriendBinding;
import com.example.survirun.server.ServerClient;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFragment extends Fragment {
    FragmentFriendBinding binding;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        String token = sf.getString("token", "");

        Call<FindUserData> call = ServerClient.getServerService().getFriendList(token);
        call.enqueue(new Callback<FindUserData>() {
            @Override
            public void onResponse(Call<FindUserData> call, Response<FindUserData> response) {
                if (response.isSuccessful()) {
                    Log.d("asdf", response.body().friends.size() + "");
                    //Log.d("asdf", response.body().friends.get(0).username+ "");


                    // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
                    FriendAdapter adapter = new FriendAdapter(response.body().friends);
                    binding.friendListRecyclerView.setAdapter(adapter);


                    binding.friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



                }
            }

            @Override
            public void onFailure(Call<FindUserData> call, Throwable t) {

            }
        });



        Log.d("토큰", token);
        binding.friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<getUserData>call1=ServerClient.getServerService().getUser(token,binding.emileInputEditText.getText().toString());
                call1.enqueue(new Callback<getUserData>() {
                    @Override
                    public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                        if(response.isSuccessful()){
                            binding.cardView.setVisibility(View.VISIBLE);
                            binding.usernameTextview.setText(response.body().username);
                            ExerciseHistory exerciseHistory=response.body().exerciseHistory.get(0);
                            binding.exerciseTextview.setText(exerciseHistory.calorie+"칼로리\n"+ exerciseHistory.km+"킬로미터\n"+exerciseHistory.time+"운동시간\n");
                            Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url",response.body().username);
                            getProfile.enqueue(new Callback<ImageData>() {
                                @Override
                                public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("adf", response.body().img);
                                        Glide.with(getContext())
                                                .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                                                .error(R.drawable.ic_profile)
                                                .into(binding.profileImageview);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ImageData> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }else{
                            Log.d("adsf","실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<getUserData> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

                binding.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email", binding.emileInputEditText.getText().toString());
                        call2.enqueue(new Callback<ResultData>() {
                            @Override
                            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                                if (response.isSuccessful()) {
                                    //성공시
                                    Log.d("adsf", response.body().result.toString());
                                } else {
                                    //실패시
                                    Log.d("asdf", binding.emileInputEditText.getText().toString());
                                    Log.d("adsf",response.code()+"");
                                    Log.d("adsf", "실패");
                                    Log.d("adsf", token);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultData> call, Throwable t) {

                            }
                        });
                    }
                });

            }
        });

        return view;
    }

}