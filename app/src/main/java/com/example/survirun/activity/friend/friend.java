package com.example.survirun.activity.friend;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class friend {
    public void addFriend(String token,String email){

        Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email",email);
        call2.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if (response.isSuccessful()) {


                } else {


                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });

    }
    public void getFriend(String token,String email){
        Call<getUserData> call1 = ServerClient.getServerService().getUser(token, email);
        call1.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {

                    Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url", response.body().username);
                    getProfile.enqueue(new Callback<ImageData>() {
                        @Override
                        public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                            if (response.isSuccessful()) {

                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ImageData> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }
    public void deleteFriend(String token,String userEmile){
        Call<ResultData> call = ServerClient.getServerService().deleteFriend(token, "email", userEmile);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if (response.isSuccessful()) {
                    class InsertRunnable implements Runnable {
                        @Override
                        public void run() {
                            try {
                               // FriendRoom friendRoom = friendDB.friendDao().findById(userEmile);
                               // friendDB.friendDao().delete(friendRoom);

                               // finish();
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


            }
        });
    }
}
