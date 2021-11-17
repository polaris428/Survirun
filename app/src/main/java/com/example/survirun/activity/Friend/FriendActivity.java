package com.example.survirun.activity.Friend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends AppCompatActivity {
    ActivityFriendBinding binding;

    String token;
    String email;
    String friendName;
    String friendEmile;
    String friendEmail;
    String profile;
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

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
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
                Toast.makeText(getApplicationContext(), R.string.you_already_friend, Toast.LENGTH_LONG).show();
            } else if (binding.emileInputEditText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), R.string.fill_error, Toast.LENGTH_LONG).show();
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
                            friendName = response.body().username;
                            friendEmile = response.body().email;
                            friendEmail = binding.emileInputEditText.getText().toString();
                            binding.cardView.setVisibility(View.VISIBLE);
                            binding.findFriendsCardView.setVisibility(View.VISIBLE);
                            binding.friendsError.setVisibility(View.GONE);
                            binding.findFriendsLoading.setVisibility(View.GONE);
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


            binding.addFriend.setOnClickListener(v1 -> {

                Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email", binding.emileInputEditText.getText().toString());
                call2.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(FriendActivity.this, R.string.success_add_friend, Toast.LENGTH_LONG).show();
                            refreshRecyclerView(friendEmile, friendName, profile);
                        } else {
                            //실패시

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {

                    }
                });
            });

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
                } catch (Exception e) {

                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();


    }
    @Override
    public void onRestart(){
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

    public void checkFriends() {//여기부터 비교해서 룸에 값넣는 코드
        Call<FindUserData> call = ServerClient.getServerService().getFriendList(token);
        call.enqueue(new Callback<FindUserData>() {
            @Override
            public void onResponse(Call<FindUserData> call, Response<FindUserData> response) {
                if (response.isSuccessful()) {
                    friendsServerNumber = response.body().users.size();
                    friendsRoomNumber = friendRoomList.size();
                    Log.d("asdf", String.valueOf(friendsServerNumber));
                    Log.d("asdf", String.valueOf(friendsRoomNumber));
                    int i = 0;
                    if (friendsRoomNumber == 0) {
                        for (i = 0; i < friendsServerNumber; i++) {
                            refreshRecyclerView(response.body().users.get(i).email, response.body().users.get(i).username, response.body().profiles.get(i)._id);

                            Log.d("뿜뿜", response.body().profiles.get(i)._id);

                        }
                    } else {
                        Boolean friend = false;
                        if (friendsServerNumber != friendsRoomNumber) {
                            for (i = 0; i < friendsServerNumber; i++) {
                                for (int j = 0; j < friendsRoomNumber; j++) {
                                    if (response.body().users.get(i).email.equals(friendRoomList.get(j).email)) {
                                        Log.d("반복중", i + "");
                                        Log.d("일치함", "일치함" + j);
                                        friend = true;
                                        break;
                                    } else {
                                        Log.d("반복중", i + "");


                                    }
                                    if (!friend) {
                                        refreshRecyclerView(response.body().users.get(i).email, response.body().users.get(i).username, response.body().profiles.get(i)._id);
                                        //String profile=response.body().users.get(i).profiles.get(i)._id;


                                    } else {
                                        friend = false;
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

        Log.d("성공", "성공");
    }
}