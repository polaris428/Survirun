package com.example.survirun.Fragmnet.Friend;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MenuRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.survirun.FriendDB;
import com.example.survirun.R;
import com.example.survirun.activity.FriendActivity;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.databinding.ActivityFriendInformationBinding;
import com.example.survirun.server.ServerClient;

import org.eazegraph.lib.models.BarModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendInformationActivity extends AppCompatActivity {
    private ActivityFriendInformationBinding binding;
    String userName;
    String userEmile;
    String userProfile;
    String token;

    Dialog dialog;

    private List<FriendRoom> friendRoomList;
    private FriendDB friendDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendInformationBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        friendDB = FriendDB.getInstance(this);

        Intent getIntent = getIntent();
        userName = getIntent.getStringExtra("userName");
        userEmile = getIntent.getStringExtra("userEmail");
        userProfile = getIntent.getStringExtra("profile");

        SharedPreferences sf = getSharedPreferences("Login", MODE_PRIVATE);
        token = sf.getString("token", "");
        Log.d("프로필", userProfile);

        binding.usernameTextview.setText(userName);
        binding.userEmailTextView.setText(userEmile);
        Glide.with(this).load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + userProfile)
                .error(R.drawable.userdefaultprofile)
                .circleCrop()
                .into(binding.userProfileImageView);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        binding.menuButton.setOnClickListener(v -> {
            showMenu(v, R.menu.friend_menu);
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);


        binding.image.startAnimation(animation);
        binding.findRecord.setVisibility(View.VISIBLE);
        Call<getUserData> call = ServerClient.getServerService().getUser(token, userEmile);
        call.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {
                    binding.calorieBarGraph.clearChart();
                    binding.goalBarGraph.clearChart();
                    binding.kmBarGraph.clearChart();
                    binding.exerciseTimeBarGraph.clearChart();
                    if (response.body().exerciseHistory.size() == 1 && response.body().exerciseHistory.get(0).time == 0) {
                        binding.noRecord.setVisibility(View.VISIBLE);
                        binding.findRecord.setVisibility(View.GONE);


                    } else {
                        binding.exerciseGraph.setVisibility(View.VISIBLE);
                        binding.ExerciseMessage.setVisibility(View.GONE);

                        for (int i = 0; i < response.body().exerciseHistory.size(); i++) {
                            int calorie = response.body().exerciseHistory.get(i).calorie;
                            double km = response.body().exerciseHistory.get(i).km;
                            String data = response.body().exerciseHistory.get(i).date;
                            int time = response.body().exerciseHistory.get(i).time;
                            Log.d("시간", time / 60 + "");
                            binding.kmBarGraph.addBar(new BarModel(data, (float) km, 0xFF56B7F1));
                            binding.calorieBarGraph.addBar(new BarModel(data, calorie, 0xFF56B7F1));
                            binding.exerciseTimeBarGraph.addBar(new BarModel(data, time, 0xFF56B7F1));
                        }
                        binding.calorieBarGraph.startAnimation();
                        binding.goalBarGraph.startAnimation();
                        binding.kmBarGraph.startAnimation();
                        binding.exerciseTimeBarGraph.startAnimation();
                    }


                } else {
                    Toast.makeText(FriendInformationActivity.this, "서버오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(FriendInformationActivity.this, "서버오류", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void showMenu(View v, @MenuRes int menuRes) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuRes, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            showDialog();
            return true;
        });
        popup.show();
    }

    private void showDialog() {
        TextView explain = dialog.findViewById(R.id.explain_textView);
        explain.setText(R.string.delete_friend_text);
        dialog.show();
        dialog.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.yes_button).setOnClickListener(v -> {
            deleteFriend();
            finish();
        });
    }

    private void deleteFriend() {
        Call<ResultData> call = ServerClient.getServerService().deleteFriend(token, "email", userEmile);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if (response.isSuccessful()) {
                    class InsertRunnable implements Runnable {
                        @Override
                        public void run() {
                            try {
                                FriendRoom friendRoom = friendDB.friendDao().findById(userEmile);
                                friendDB.friendDao().delete(friendRoom);
                            } catch (Exception e) {

                            }
                        }
                    }
                    InsertRunnable insertRunnable = new InsertRunnable();
                    Thread t = new Thread(insertRunnable);
                    t.start();
                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(FriendInformationActivity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}