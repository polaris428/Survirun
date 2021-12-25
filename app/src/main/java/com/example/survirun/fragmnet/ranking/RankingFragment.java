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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
    Boolean ranking = false;
    Boolean isLoading = true;
    RequestManager glide ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        glide = Glide.with(getContext());
        /*Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        binding.image.startAnimation(animation);

        binding.findRanking.setVisibility(View.VISIBLE);*/

        sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        token = sf.getString("token", "");

        binding.friendButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FriendActivity.class));
        });

        binding.rankingModeButton.setOnClickListener(v -> {
            binding.rankingMessage.setVisibility(View.GONE);
            binding.noFriend.setVisibility(View.GONE);
            isLoading = true;
            showSampleData();
            binding.rankingModeButton.setEnabled(false);
            if (ranking) {
                ranking = false;
                rankinDataList.clear();
                binding.rankingModeButton.setText(R.string.all);
                RankingAdapter RankingAdapter = new RankingAdapter(rankinDataList,glide );
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
                                    RankingAdapter RinkingAdapter = new RankingAdapter(rankinDataList, glide);
                                    binding.rankingRecyclerView.setAdapter(RinkingAdapter);
                                    isLoading = false;
                                    showSampleData();
                                    binding.rankingMessage.setVisibility(View.GONE);
                                    binding.rankingModeButton.setEnabled(true);
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
                upDataFriend();

                friendRankingList.clear();
                binding.rankingModeButton.setText(R.string.friends);
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
                    binding.rankingRecyclerView.setVisibility(View.GONE);
                    binding.sfLayout.stopShimmer();
                    binding.sfLayout.setVisibility(View.GONE);
                    binding.rankingMessage.setVisibility(View.VISIBLE);
                    binding.noFriend.setVisibility(View.VISIBLE);
                    binding.rankingModeButton.setEnabled(true);
                } else {
                    RankingAdapter RankingAdapter = new RankingAdapter(friendRankingList, glide);
                    binding.rankingRecyclerView.setAdapter(RankingAdapter);
                    binding.rankingModeButton.setEnabled(true);
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        upDataFriend();
        isLoading = true;
        showSampleData();
        rankinDataList.clear();
        ranking = false;
        binding.rankingModeButton.setText(R.string.all);

        Call<rankingData> call = ServerClient.getServerService().getRanking(token);
        call.enqueue(new Callback<rankingData>() {
            @Override
            public void onResponse(Call<rankingData> call, Response<rankingData> response) {
                if (response.isSuccessful()) {
                    Log.d("성공", "성공");
                    for (int i = 0; i < response.body().scores.size(); i++) {
                        UpData(response.body().users.get(i).username, response.body().users.get(i).email, response.body().scores.get(i));
                        if (response.body().scores.size() - 1 == i) {
                            RankingAdapter RinkingAdapter = new RankingAdapter(rankinDataList, glide);
                            binding.rankingRecyclerView.setAdapter(RinkingAdapter);
                            isLoading = false;
                            showSampleData();
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
    }


    public void UpData(String userName, String userEmail, int userScore) {
        RankingData rankinData = new RankingData();
        rankinData.setUsername(userName);
        rankinData.setUserEmail(userEmail);
        rankinData.setScore(userScore);
        rankinDataList.add(rankinData);
    }

    private void upDataFriend() {
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
    }

    private void showSampleData() {
        if (isLoading) {
            binding.sfLayout.startShimmer();
            binding.sfLayout.setVisibility(View.VISIBLE);
            binding.rankingRecyclerView.setVisibility(View.GONE);
            binding.rankingMessage.setVisibility(View.GONE);
        } else {
            binding.sfLayout.stopShimmer();
            binding.sfLayout.setVisibility(View.GONE);
            binding.rankingRecyclerView.setVisibility(View.VISIBLE);
            binding.rankingMessage.setVisibility(View.GONE);
        }
    }
}