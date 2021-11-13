package com.example.survirun;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Toast;

import com.example.survirun.data.ExerciseData;
import com.example.survirun.data.ExerciseRecordData;
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
    public void yesterdayExercise(String token,Context context){
        Call<ExerciseRecordData> call= ServerClient.getServerService().getExerciseRecordData(token);
        call.enqueue(new Callback<ExerciseRecordData>() {
            @Override
            public void onResponse(Call<ExerciseRecordData> call, Response<ExerciseRecordData> response) {
                if(response.isSuccessful()) {
                    SharedPreferences sf;
                    SharedPreferences.Editor editor;
                    sf = context.getSharedPreferences("yesterdayExercise", MODE_PRIVATE);
                    editor=sf.edit();
                    if (response.body().exerciseHistory.size()==1){
                        editor.putInt("calorie", 0);
                        editor.putFloat("km", 0);
                        editor.putInt("time",0);
                        editor.commit();
                    }else{

                        int exSize=response.body().exerciseHistory.size()-2;

                        editor.putInt("calorie", response.body().exerciseHistory.get(exSize).calorie);
                        editor.putFloat("km", (float) response.body().exerciseHistory.get(exSize).km);
                        editor.putInt("time",response.body().exerciseHistory.get(exSize).time);
                        editor.commit();
                    }

                }

            }

            @Override
            public void onFailure(Call<ExerciseRecordData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context,"서버오류 잠시후 다시 시도해주세요",Toast.LENGTH_LONG);

            }
        });


    }
}
