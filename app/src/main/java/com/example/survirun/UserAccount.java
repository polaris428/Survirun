package com.example.survirun;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.example.survirun.data.ExerciseData;
import com.example.survirun.server.ServerClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAccount {
    public void getUser(String token, Context context){


        Call<ExerciseData> call = ServerClient.getServerService().getExercise(token);
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    SharedPreferences sf;

                    SharedPreferences.Editor editor;
                    sf = context.getSharedPreferences("exercise", MODE_PRIVATE);
                    editor=sf.edit();
                    editor.putString("data", response.body().date);
                    editor.putInt("calorie", response.body().calorie);
                    editor.putFloat("km", (float) response.body().km);
                    editor.putInt("time",response.body().time);
                    editor.commit();


                }

            }


            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {

            }
        });




    }
}
