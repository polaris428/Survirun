package com.example.survirun.Fragmnet.Friend;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    String token;
    String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
         token = sf.getString("token", "");
         email = sf.getString("email", "");


        Log.d("토큰", token);
        binding.backButton.setOnClickListener(v -> {
            binding.findFriendsCardView.setVisibility(View.GONE);
            binding.friendsError.setVisibility(View.GONE);
            binding.cardView.setVisibility(View.GONE);
        });
        binding.friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getFriend();
        binding.findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.equals(binding.emileInputEditText.getText().toString())) {
                    Toast.makeText(getContext(), "자기 자신의 이미 최고의 친구입니다", Toast.LENGTH_LONG).show();
                }else if(binding.emileInputEditText.getText().toString().equals("")){
                    Toast.makeText(getContext(), "공백을 채워주세요", Toast.LENGTH_LONG).show();
                } else {
                    binding.findFriendsCardView.setVisibility(View.GONE);
                    binding.friendsError.setVisibility(View.GONE);
                    binding.cardView.setVisibility(View.GONE);
                    Call<getUserData> call1 = ServerClient.getServerService().getUser(token, binding.emileInputEditText.getText().toString());
                    call1.enqueue(new Callback<getUserData>() {
                        @Override
                        public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                            if (response.isSuccessful()) {
                                binding.cardView.setVisibility(View.VISIBLE);
                                binding.findFriendsCardView.setVisibility(View.VISIBLE);
                                binding.friendsError.setVisibility(View.GONE);
                                binding.usernameTextview.setText(response.body().username);
                                ExerciseHistory exerciseHistory = response.body().exerciseHistory.get(0);
                                binding.exerciseTextview.setText(exerciseHistory.calorie + "칼로리 " + exerciseHistory.km + "킬로미터 " + exerciseHistory.time + "시간 ");
                                Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url", response.body().username);
                                getProfile.enqueue(new Callback<ImageData>() {
                                    @Override
                                    public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                                        if (response.isSuccessful()) {
                                            if (getActivity() == null) {
                                                return;
                                            }
                                            Log.d("adf", response.body().img);
                                            Glide.with(FriendFragment.this)
                                                    .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                                                    .error(R.drawable.ic_profile)
                                                    .circleCrop()
                                                    .into(binding.profileImageview);
                                        }else {
                                            binding.cardView.setVisibility(View.VISIBLE);
                                            binding.friendsError.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ImageData> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            } else {

                                binding.cardView.setVisibility(View.VISIBLE);
                                binding.friendsError.setVisibility(View.VISIBLE);

                            }
                        }

                        @Override
                        public void onFailure(Call<getUserData> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }


                binding.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email", binding.emileInputEditText.getText().toString());
                        call2.enqueue(new Callback<ResultData>() {
                            @Override
                            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                                if (response.isSuccessful()) {
                                    getFriend();
                                    Log.d("성공", response.body().result.toString());
                                } else {
                                    //실패시

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
    void getFriend(){
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


    }
}