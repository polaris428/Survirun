package com.example.survirun.Fragmnet.Friend;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.data.FindUserData;
import com.example.survirun.data.Friends;
import com.example.survirun.data.ResultData;
import com.example.survirun.databinding.FragmentFriendBinding;
import com.example.survirun.server.ServerClient;
import com.example.survirun.server.ServiceService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

                    ArrayList list = new ArrayList();
                    FriendAdapter adapter = new FriendAdapter(list);
                    binding.friendListRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<FindUserData> call, Throwable t) {

            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResultData> call1 = ServerClient.getServerService().PostFindFriend(token, binding.emileInputEditText.getText().toString());
                call1.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {

                    }
                });
            }
        });

        Log.d("토큰", token);
        binding.friendListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResultData> call1 = ServerClient.getServerService().postAddFriend(token, "", binding.emileInputEditText.getText().toString());
                call1.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful()) {
                            Log.d("adsf", response.body().result.toString());
                        } else {
                            Log.d("asdf", binding.emileInputEditText.getText().toString());
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
        return view;
    }
}