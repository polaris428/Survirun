package com.example.survirun.activity.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.service.autofill.UserData;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.data.ExerciseData;
import com.example.survirun.data.ExerciseRecordData;
import com.example.survirun.data.getUserData;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAccount {
    public void getExercise(String token, Context context) {


        Call<ExerciseData> call = ServerClient.getServerService().getExercise(token);
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    SharedPreferences sf;

                    SharedPreferences.Editor editor;
                    sf = context.getSharedPreferences("exercise", MODE_PRIVATE);
                    editor = sf.edit();
                    editor.putString("data", response.body().date);
                    editor.putInt("calorie", response.body().calorie);
                    editor.putFloat("km", (float) response.body().km);
                    editor.putInt("time", response.body().time);
                    editor.commit();


                }

            }


            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {

            }
        });


    }

    public void yesterdayExercise(String token, Context context) {
        Call<ExerciseRecordData> call = ServerClient.getServerService().getExerciseRecordData(token);
        call.enqueue(new Callback<ExerciseRecordData>() {
            @Override
            public void onResponse(Call<ExerciseRecordData> call, Response<ExerciseRecordData> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sf;
                    SharedPreferences.Editor editor;
                    sf = context.getSharedPreferences("yesterdayExercise", MODE_PRIVATE);
                    editor = sf.edit();
                    if (response.body().exerciseHistory.size() == 1) {
                        editor.putInt("calorie", 0);
                        editor.putFloat("km", 0);
                        editor.putInt("time", 0);
                        editor.commit();
                    } else {

                        int exSize = response.body().exerciseHistory.size() - 2;

                        editor.putInt("calorie", response.body().exerciseHistory.get(exSize).calorie);
                        editor.putFloat("km", (float) response.body().exerciseHistory.get(exSize).km);
                        editor.putInt("time", response.body().exerciseHistory.get(exSize).time);
                        editor.commit();
                    }

                }

            }

            @Override
            public void onFailure(Call<ExerciseRecordData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, R.string.error_server, Toast.LENGTH_LONG);

            }
        });


    }

    public void getUser(String token, String email, Context context) {
        Call<getUserData> getUser = ServerClient.getServerService().getUser(token, email);
        getUser.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {
                    SharedPreferences sf;
                    SharedPreferences.Editor editor;
                    sf = context.getSharedPreferences("Login", MODE_PRIVATE);
                    editor=sf.edit();

                    editor.putString("intro", response.body().intro);
                    sf = context.getSharedPreferences("yesterdayExercise", MODE_PRIVATE);
                    editor = sf.edit();
                    editor.putString("name", response.body().username);

                    editor.putInt("score", response.body().score);
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }
}
