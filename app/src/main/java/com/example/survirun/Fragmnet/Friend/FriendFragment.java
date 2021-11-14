package com.example.survirun.Fragmnet.Friend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.Fragmnet.SettingFragment;
import com.example.survirun.FriendDB;
import com.example.survirun.FriendDao;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FindUserData;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.Friends;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.FragmentFriendBinding;
import com.example.survirun.server.ServerClient;
import com.google.android.material.progressindicator.BaseProgressIndicator;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFragment extends Fragment {
    FragmentFriendBinding binding;

    String token;
    String email;
    String name;
    String friendEmail;
    String profile;
    int friendsServerNumber, friendsRoomNumber;
    private List<FriendRoom> friendRoomList;
    private FriendDB friendDB = null;
    private Context mContext = null;
    private FriendAdapter friendAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        token = sf.getString("token", "");
        email = sf.getString("email", "");

        friendDB = FriendDB.getInstance(getContext());
        mContext = getContext().getApplicationContext();
        friendAdapter = new FriendAdapter(friendRoomList);
        binding.cardView.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);
        checkFriends();

        binding.image.startAnimation(animation);


        Log.d("토큰", token);
        binding.backButton.setOnClickListener(v -> {
            binding.findFriendsCardView.setVisibility(View.GONE);
            binding.friendsError.setVisibility(View.GONE);
            binding.cardView.setVisibility(View.GONE);
        });


        //binding.friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.findFriends.setOnClickListener(v -> {
            friendEmail = binding.emileInputEditText.getText().toString();
            if (email.equals(friendEmail)) {
                Toast.makeText(getContext(), R.string.you_already_friend, Toast.LENGTH_LONG).show();
            } else if (binding.emileInputEditText.getText().toString().equals("")) {
                Toast.makeText(getContext(), R.string.fill_error, Toast.LENGTH_LONG).show();
            } else {
                binding.findFriendsLoading.setVisibility(View.VISIBLE);
                binding.backButton.setVisibility(View.GONE);
                binding.findFriendsCardView.setVisibility(View.GONE);
                binding.friendsError.setVisibility(View.GONE);
                binding.cardView.setVisibility(View.VISIBLE);
                Call<getUserData> call1 = ServerClient.getServerService().getUser(token, binding.emileInputEditText.getText().toString());
                call1.enqueue(new Callback<getUserData>() {
                    @Override
                    public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                        if (response.isSuccessful()) {
                            name = response.body().username;
                            friendEmail = binding.emileInputEditText.getText().toString();
                            binding.cardView.setVisibility(View.VISIBLE);
                            binding.findFriendsCardView.setVisibility(View.VISIBLE);
                            binding.friendsError.setVisibility(View.GONE);
                            binding.findFriendsLoading.setVisibility(View.GONE);
                            binding.backButton.setVisibility(View.VISIBLE);
                            binding.usernameTextview.setText(name);
                            ExerciseHistory exerciseHistory = response.body().exerciseHistory.get(0);
                            binding.exerciseTextview.setText(exerciseHistory.calorie + "kcal " + exerciseHistory.km + "km " + exerciseHistory.time + getString(R.string.hour));
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
                                                .error(R.drawable.userdefaultprofile)
                                                .circleCrop()
                                                .into(binding.profileImageview);
                                        profile = "https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img;

                                    } else {
                                        binding.findFriendsLoading.setVisibility(View.GONE);
                                        binding.backButton.setVisibility(View.VISIBLE);
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
                            binding.findFriendsLoading.setVisibility(View.GONE);
                            binding.backButton.setVisibility(View.VISIBLE);
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
                                class InsertRunnable implements Runnable {
                                    @Override
                                    public void run() {
                                        FriendRoom friendRoom = new FriendRoom();
                                        friendRoom.email = friendEmail;
                                        friendRoom.profile = profile;
                                        friendRoom.name = name;
                                        FriendDB.getInstance(mContext).friendDao().insertAll(friendRoom);
                                    }
                                }
                                InsertRunnable insertRunnable = new InsertRunnable();
                                Thread addThread = new Thread(insertRunnable);
                                addThread.start();
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

        });

        return view;
    }

    void getFriend() {
        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    friendRoomList = FriendDB.getInstance(mContext).friendDao().getAll();
                    friendAdapter = new FriendAdapter(friendRoomList);
                    friendAdapter.notifyDataSetChanged();

                    binding.friendListRecyclerView.setAdapter(friendAdapter);
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                    binding.friendListRecyclerView.setLayoutManager(mLinearLayoutManager);
                } catch (Exception e) {

                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkFriends();
        FriendDB.destroyInstance();
        friendDB = null;
    }

    public void checkFriends() {//여기부터 비교해서 룸에 값넣는 코드
        Call<FindUserData> call = ServerClient.getServerService().getFriendList(token);
        call.enqueue(new Callback<FindUserData>() {
            @Override
            public void onResponse(Call<FindUserData> call, Response<FindUserData> response) {
                if (response.isSuccessful()) {
                    friendsServerNumber = response.body().friends.size();
                    friendsRoomNumber = friendRoomList.size();
                    Log.d("asdf", String.valueOf(friendsServerNumber));
                    Log.d("asdf", String.valueOf(friendsRoomNumber));
                    int i = 0;
                    if (friendsRoomNumber == 0) {
                        for (i = 0; i < friendsServerNumber; i++) {
                            Log.d("일차하지않음", "일치하지않음" + response.body().friends.get(i).email);
                            addFriend(response.body().friends.get(i).email);
                        }
                    } else {
                        Boolean friend = false;
                        if (friendsServerNumber != friendsRoomNumber) {
                            for (i = 0; i < friendsServerNumber; i++) {
                                for (int j = 0; j < friendsRoomNumber; j++) {
                                    if (response.body().friends.get(i).email.equals(friendRoomList.get(j).email)) {
                                        Log.d("반복중", i + "");
                                        Log.d("일치함", "일치함" + j);
                                        friend = true;
                                        break;
                                    } else {
                                        Log.d("반복중", i + "");
                                        Log.d("일차하지않음", "일치하지않음" + j);


                                    }
                                    if (!friend) {
                                        Log.d("이메일", response.body().friends.get(i).email);
                                        addFriend(response.body().friends.get(i).email);
                                    }

                                }
                            }
                            Log.d("반복된 횟수", i + "");
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<FindUserData> call, Throwable t) {
            }

        });
        getFriend();
    }

    public void addFriend(String friendEmail) {
        Call<getUserData> getUserDataCall = ServerClient.getServerService().getUser(token, friendEmail);
        getUserDataCall.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {
                    name = response.body().username;
                    Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
                    getProfile.enqueue(new Callback<ImageData>() {
                        @Override
                        public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                            if (response.isSuccessful()) {
                                profile = "https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img;
                                class InsertRunnable implements Runnable {
                                    @Override
                                    public void run() {
                                        FriendRoom friendRoom = new FriendRoom();
                                        friendRoom.email = friendEmail;
                                        friendRoom.profile = profile;
                                        friendRoom.name = name;
                                        FriendDB.getInstance(mContext).friendDao().insertAll(friendRoom);
                                    }
                                }
                                InsertRunnable insertRunnable = new InsertRunnable();
                                Thread addThread = new Thread(insertRunnable);
                                addThread.start();
                                getFriend();
                            }
                        }

                        @Override
                        public void onFailure(Call<ImageData> call, Throwable t) {

                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {

            }
        });

    }
}