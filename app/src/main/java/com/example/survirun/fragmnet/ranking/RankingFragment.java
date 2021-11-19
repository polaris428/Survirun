package com.example.survirun.fragmnet.ranking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.fragment.app.Fragment;

import com.example.survirun.FriendDB;
import com.example.survirun.R;
import com.example.survirun.activity.friend.FriendActivity;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.rankingData;
import com.example.survirun.databinding.FragmentRankingBinding;
import com.example.survirun.server.ServerClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {

    FragmentRankingBinding binding;

    SharedPreferences sf;
    String token;
    List<RankingData> rankinDataList = new ArrayList<>();
    List<RankingData> friendRankingList = new ArrayList<>();
    List<FriendRoom> friendRoomList;
    boolean ranking = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    friendRoomList = FriendDB.getInstance(getActivity()).friendDao().getAll();

                } catch (Exception e) {

                }
            }
        }
        InsertRunnable insertRunnable = new InsertRunnable();
        Thread t = new Thread(insertRunnable);
        t.start();

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        binding.image.startAnimation(animation);

        binding.findRanking.setVisibility(View.VISIBLE);

        sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        token = sf.getString("token", "");

        binding.friendButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FriendActivity.class));
        });

        binding.rankingModeButton.setOnClickListener(v -> {
            binding.rankingMessage.setVisibility(View.VISIBLE);
            binding.noFriend.setVisibility(View.GONE);
            binding.findRanking.setVisibility(View.VISIBLE);
            if (ranking) {
                ranking = false;
                rankinDataList.clear();
                binding.rankingModeButton.setText("전체");
                RankingAdapter RankingAdapter = new RankingAdapter(rankinDataList);
                binding.rankingRecyclerView.setAdapter(RankingAdapter);
                Call<rankingData> call = ServerClient.getServerService().getRanking(token);
                call.enqueue(new Callback<rankingData>() {
                    @Override
                    public void onResponse(Call<rankingData> call, Response<rankingData> response) {
                        if (response.isSuccessful()) {
                            Log.d("성공", "성공");
                            for (int i = 0; i < response.body().scores.size(); i++) {
                                UpData(response.body().users.get(i).username, response.body().users.get(i).email, response.body().scores.get(i));
                                if (response.body().scores.size() - 1 == i) {
                                    RankingAdapter RinkingAdapter = new RankingAdapter(rankinDataList);
                                    binding.rankingRecyclerView.setAdapter(RinkingAdapter);
                                    binding.rankingRecyclerView.setVisibility(View.VISIBLE);
                                    binding.rankingMessage.setVisibility(View.GONE);
                                }
                            }


                        } else {
                            Log.d("실패", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<rankingData> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else {
                binding.rankingModeButton.setText("친구");
                ranking = true;
                friendRankingList.clear();
                for (int i = 0; i < rankinDataList.size(); i++) {
                    for (int j = 0; j < friendRoomList.size(); j++) {
                        if (rankinDataList.get(i).userEmail.equals(friendRoomList.get(j).email)) {
                            Log.d("겹침", rankinDataList.get(i).userName);
                            RankingData friendRankingData = new RankingData();
                            friendRankingData.userEmail = rankinDataList.get(i).userEmail;
                            friendRankingData.userName = rankinDataList.get(i).userName;
                            friendRankingData.userScore = rankinDataList.get(i).userScore;
                            friendRankingList.add(friendRankingData);
                        }
                    }

                }
                if (friendRankingList.size() == 0) {
                    binding.rankingMessage.setVisibility(View.VISIBLE);
                    binding.noFriend.setVisibility(View.VISIBLE);
                    binding.findRanking.setVisibility(View.GONE);
                    binding.rankingRecyclerView.setVisibility(View.GONE);
                } else {
                    RankingAdapter RankingAdapter = new RankingAdapter(friendRankingList);
                    binding.rankingRecyclerView.setAdapter(RankingAdapter);
                    binding.rankingRecyclerView.setVisibility(View.VISIBLE);
                    binding.rankingMessage.setVisibility(View.GONE);
                }
            }
        });


        RankingAdapter RankingAdapter = new RankingAdapter(rankinDataList);
        binding.rankingRecyclerView.setAdapter(RankingAdapter);
        Call<rankingData> call = ServerClient.getServerService().getRanking(token);
        call.enqueue(new Callback<rankingData>() {
            @Override
            public void onResponse(Call<rankingData> call, Response<rankingData> response) {
                if (response.isSuccessful()) {
                    Log.d("성공", "성공");
                    for (int i = 0; i < response.body().scores.size(); i++) {
                        UpData(response.body().users.get(i).username, response.body().users.get(i).email, response.body().scores.get(i));
                        if (response.body().scores.size() - 1 == i) {
                            RankingAdapter RinkingAdapter = new RankingAdapter(rankinDataList);
                            binding.rankingRecyclerView.setAdapter(RinkingAdapter);
                            binding.rankingMessage.setVisibility(View.GONE);
                            binding.rankingRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Log.d("실패", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<rankingData> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return view;
    }


    public void UpData(String userName, String userEmail, int userScore) {
        RankingData rankinData = new RankingData();
        rankinData.setUsername(userName);
        rankinData.setUserEmail(userEmail);
        rankinData.setScore(userScore);
        rankinDataList.add(rankinData);
    }


}