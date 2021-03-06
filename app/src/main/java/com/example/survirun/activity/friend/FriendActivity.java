package com.example.survirun.activity.friend;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.FriendDB;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FindUserData;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.ActivityFriendBinding;
import com.example.survirun.server.ServerClient;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends AppCompatActivity {
    ActivityFriendBinding binding;

    String token;
    String email;
    String friendName;
    String getFriendEmail;
    String friendEmail;
    String profile;

    Boolean isLoading = true;
    int friendsServerNumber, friendsRoomNumber;
    private List<FriendRoom> friendRoomList;
    private FriendDB friendDB = null;
    private Context mContext = null;
    private FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        email = sf.getString("email", "");

        friendDB = FriendDB.getInstance(this);
        mContext = getApplicationContext();

        friendAdapter = new FriendAdapter(friendRoomList);
        friendAdapter.setHasStableIds(true);

        binding.cardView.setVisibility(View.GONE);

        //Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        checkFriends();

        //binding.image.startAnimation(animation);
        showSampleData();


        binding.findFriends.setOnClickListener(v -> {
            binding.findFriends.setVisibility(View.INVISIBLE);
            binding.emailInputLayout.setVisibility(View.VISIBLE);
            ValueAnimator anim = ValueAnimator.ofInt(binding.findFriends.getWidth(), binding.emailInputLayout.getWidth());
            setAnimation(anim, binding.emailInputLayout);
        });

        binding.emileInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Handler handler = new Handler();
                friendEmail = binding.emileInputEditText.getText().toString();
                if (friendEmail.equals(email)) {
                    noShow();
                    binding.shimmerLayout.stopShimmer();
                    Toast.makeText(getApplicationContext(), R.string.you_already_friend, Toast.LENGTH_LONG).show();
                } else if (binding.emileInputEditText.getText().toString().equals("")) {
                    noShow();
                    binding.shimmerLayout.stopShimmer();
                } else {
                    loading();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Call<getUserData> call1 = ServerClient.getServerService().getUser(token, binding.emileInputEditText.getText().toString());
                            call1.enqueue(new Callback<getUserData>() {
                                @Override
                                public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                                    if (response.isSuccessful()) {
                                        friendName = response.body().username;
                                        getFriendEmail = response.body().email;
                                        binding.cardView.setVisibility(View.VISIBLE);
                                        binding.findFriendsCardView.setVisibility(View.VISIBLE);
                                        binding.friendsError.setVisibility(View.GONE);
                                        binding.friendsShimmerCardView.setVisibility(View.GONE);
                                        binding.shimmerLayout.stopShimmer();
                                        binding.backButton.setVisibility(View.VISIBLE);
                                        binding.usernameTextview.setText(friendName);
                                        ExerciseHistory exerciseHistory = response.body().exerciseHistory.get(0);
                                        binding.exerciseTextview.setText(exerciseHistory.calorie + "kcal " + exerciseHistory.time + getString(R.string.hour) + " " + exerciseHistory.km + "km ");

                                        Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url", response.body().username);
                                        getProfile.enqueue(new Callback<ImageData>() {
                                            @Override
                                            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                                                if (response.isSuccessful()) {
                                                    if (getApplicationContext() == null) {
                                                        return;
                                                    }
                                                    Log.d("adf", response.body().img);
                                                    Glide.with(FriendActivity.this)
                                                            .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                                                            .error(R.drawable.userdefaultprofile)
                                                            .placeholder(R.drawable.ic_userprofile)
                                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                            .skipMemoryCache(true)
                                                            .circleCrop()
                                                            .into(binding.profileImageview);
                                                    profile = "https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img;

                                                } else {
                                                    error();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ImageData> call, Throwable t) {
                                                t.printStackTrace();
                                            }
                                        });
                                    } else {
                                        error();
                                        binding.shimmerLayout.stopShimmer();
                                    }
                                }

                                @Override
                                public void onFailure(Call<getUserData> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                        }
                    }, 1000);
                }


                binding.addFriend.setOnClickListener(v1 -> {
                    if(email.equals( binding.emileInputEditText.getText().toString())){
                        Toast.makeText(getApplicationContext(), R.string.you_already_friend, Toast.LENGTH_LONG).show();

                    }else{
                        Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email", binding.emileInputEditText.getText().toString());
                        call2.enqueue(new Callback<ResultData>() {
                            @Override
                            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(FriendActivity.this, R.string.success_add_friend_text, Toast.LENGTH_LONG).show();
                                    refreshRecyclerView(getFriendEmail, friendName, profile);
                                } else {


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


        Log.d("??????", token);
        binding.backButton.setOnClickListener(v -> {
            binding.findFriendsCardView.setVisibility(View.GONE);
            binding.friendsError.setVisibility(View.GONE);
            binding.cardView.setVisibility(View.GONE);
        });

        binding.mainBackButton.setOnClickListener(v -> {
            finish();
        });

    }

    void getFriend() {
        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    friendRoomList = FriendDB.getInstance(mContext).friendDao().getAll();
                    friendAdapter = new FriendAdapter(friendRoomList);
                    binding.friendListRecyclerView.setAdapter(friendAdapter);
                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                    binding.friendListRecyclerView.setLayoutManager(mLinearLayoutManager);
                    isLoading = false;
                } catch (Exception e) {

                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();
        showSampleData();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        getFriend();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkFriends();
        FriendDB.destroyInstance();
        friendDB = null;
    }

    public void checkFriends() {//???????????? ???????????? ?????? ????????? ??????
        Call<FindUserData> call = ServerClient.getServerService().getFriendList(token);
        call.enqueue(new Callback<FindUserData>() {
            @Override
            public void onResponse(Call<FindUserData> call, Response<FindUserData> response) {
                if (response.isSuccessful()) {
                    friendsServerNumber = response.body().users.size();
                    friendsRoomNumber = friendRoomList.size();
                    Log.d("??????", String.valueOf(friendsServerNumber));
                    Log.d("???", String.valueOf(friendsRoomNumber));
                    int i = 0;
                    int j = 0;
                    if (friendsRoomNumber == 0) {
                        for (i = 0; i < friendsServerNumber; i++) {
                            refreshRecyclerView(response.body().users.get(i).email, response.body().users.get(i).username, response.body().profiles.get(i)._id);

                            Log.d("??????", response.body().profiles.get(i)._id);

                        }
                    } else if (friendsServerNumber == friendsRoomNumber) {
                        Boolean friend = false;
                        for (i = 0; i < friendsServerNumber; i++) {
                            for (j = 0; j < friendsRoomNumber; j++) {
                                if (response.body().users.get(i).email.equals(friendRoomList.get(j).email)) {
                                    if (!response.body().users.get(i).username.equals(friendRoomList.get(j).name)) {
                                        String changeName = friendRoomList.get(j).name;
                                        class InsertRunnable implements Runnable {
                                            @Override
                                            public void run() {
                                                try {
                                                    FriendRoom friendRoom = friendDB.friendDao().findById(changeName);
                                                    friendDB.friendDao().delete(friendRoom);

                                                    checkFriends();
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                        InsertRunnable insertRunnable = new InsertRunnable();
                                        Thread t = new Thread(insertRunnable);
                                        t.start();
                                    }
                                    friend = true;
                                    break;
                                } else {
                                    Log.d("?????????", i + "");


                                }
                                if (!friend) {
                                    refreshRecyclerView(response.body().users.get(i).email, response.body().users.get(i).username, response.body().profiles.get(i)._id);
                                    //String profile=response.body().users.get(i).profiles.get(i)._id;


                                } else {
                                    friend = false;
                                }

                            }
                        }


                    } else {
                        if (friendsServerNumber > friendsRoomNumber) {
                            Log.d("????????? ????????? ??????", "");
                            Boolean friend = false;

                            for (i = 0; i < friendsServerNumber; i++) {
                                for (j = 0; j < friendsRoomNumber; j++) {
                                    if (response.body().users.get(i).email.equals(friendRoomList.get(j).email)) {
                                        Log.d("?????????", i + "");
                                        Log.d("?????????", "?????????" + j);
                                        friend = true;
                                        break;
                                    } else {
                                        Log.d("?????????", i + "");


                                    }
                                    if (!friend) {
                                        refreshRecyclerView(response.body().users.get(i).email, response.body().users.get(i).username, response.body().profiles.get(i)._id);
                                        //String profile=response.body().users.get(i).profiles.get(i)._id;


                                    } else {
                                        friend = false;
                                    }

                                }
                            }
                            Log.d("????????? ??????", i + "");
                        }
                        if (friendsRoomNumber > friendsServerNumber) {
                            Boolean friend = false;

                            for (i = 0; i < friendsRoomNumber; i++) {
                                for (j = 0; j < friendsServerNumber; j++) {
                                    if (response.body().users.get(j).email.equals(friendRoomList.get(i).email)) {
                                        Log.d("?????????", i + "");
                                        Log.d("?????????", "?????????" + j);
                                        friend = true;
                                        break;
                                    } else {
                                        Log.d("?????????", i + "");


                                    }

                                }
                                if (!friend) {

                                    Log.d("????????? ??????", friendRoomList.get(i).email);
                                    String username = friendRoomList.get(i).email;
                                    //String profile=response.body().users.get(i).profiles.get(i)._id;

                                    class InsertRunnable implements Runnable {
                                        @Override
                                        public void run() {
                                            try {
                                                FriendRoom friendRoom = friendDB.friendDao().findById(username);
                                                friendDB.friendDao().delete(friendRoom);

                                            } catch (Exception e) {

                                            }
                                        }
                                    }
                                    InsertRunnable insertRunnable = new InsertRunnable();
                                    Thread t = new Thread(insertRunnable);
                                    t.start();
                                } else {
                                    friend = false;
                                }


                            }
                            Log.d("????????? ??????", i + "");


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

    public void refreshRecyclerView(String email, String name, String profile) {
        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                FriendRoom friendRoom = new FriendRoom();
                friendRoom.email = email;
                friendRoom.profile = profile;
                friendRoom.name = name;
                FriendDB.getInstance(mContext).friendDao().insertAll(friendRoom);

            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread addThread = new Thread(insertRunnable);
        addThread.start();
        getFriend();
        FriendRoom friendRoom = new FriendRoom();
        friendRoom.setEmail(email);
        friendRoom.setName(name);
        friendRoom.setProfile(profile);
        friendRoomList.add(friendRoom);
        friendAdapter = new FriendAdapter(friendRoomList);
        binding.friendListRecyclerView.setAdapter(friendAdapter);
        binding.cardView.setVisibility(View.GONE);

        Log.d("??????", "??????");
    }

    private void setAnimation(ValueAnimator anim, TextInputLayout textInputLayout) {
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            textInputLayout.getLayoutParams().width = value.intValue();
            textInputLayout.requestLayout();
        });
        anim.start();
    }

    private void loading() {
        binding.friendsShimmerCardView.setVisibility(View.VISIBLE);
        binding.backButton.setVisibility(View.GONE);
        binding.findFriendsCardView.setVisibility(View.GONE);
        binding.friendsError.setVisibility(View.GONE);
        binding.cardView.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();
    }

    private void noShow() {
        binding.findFriendsCardView.setVisibility(View.GONE);
        binding.friendsError.setVisibility(View.GONE);
        binding.cardView.setVisibility(View.GONE);
    }

    private void error() {
        binding.friendsShimmerCardView.setVisibility(View.GONE);
        binding.backButton.setVisibility(View.VISIBLE);
        binding.cardView.setVisibility(View.VISIBLE);
        binding.friendsError.setVisibility(View.VISIBLE);
    }

    private void showSampleData() {
        if (isLoading) {
            binding.sfLayout.startShimmer();
            binding.sfLayout.setVisibility(View.VISIBLE);
            binding.friendListRecyclerView.setVisibility(View.GONE);
        } else {
            binding.sfLayout.stopShimmer();
            binding.sfLayout.setVisibility(View.GONE);
            binding.friendListRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}