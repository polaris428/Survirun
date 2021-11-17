package com.example.survirun.Fragmnet.ranking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.survirun.activity.Friend.FriendActivity;
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
    List<RankinData>rankinDataList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        token = sf.getString("token", "");
        binding.friendButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FriendActivity.class));
        });
        RinkingAdapter RinkingAdapter = new RinkingAdapter(rankinDataList);
        binding.rankingRecyclerView.setAdapter(RinkingAdapter);
        Call<rankingData>call = ServerClient.getServerService().getRanking(token);
        call.enqueue(new Callback<rankingData>() {
            @Override
            public void onResponse(Call<rankingData> call, Response<rankingData> response) {
                if(response.isSuccessful()){
                    Log.d("성공","성공");
                    for(int i=0;i<response.body().scores.size();i++){
                        UpData(response.body().users.get(i).username,response.body().users.get(i).email,response.body().scores.get(i));
                        if(response.body().scores.size()-1==i){
                            RinkingAdapter RinkingAdapter = new RinkingAdapter(rankinDataList);
                            binding.rankingRecyclerView.setAdapter(RinkingAdapter);
                        }
                    }


                }else{
                    Log.d("실패",response.errorBody().toString());
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
        RankinData rankinData =new RankinData();
        rankinData.setUsername(userName);
        rankinData.setUserEmail(userEmail);
        rankinData.setScore(userScore);
        rankinDataList.add(rankinData);
    }


}