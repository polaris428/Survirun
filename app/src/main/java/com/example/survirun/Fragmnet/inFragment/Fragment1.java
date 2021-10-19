package com.example.survirun.Fragmnet.inFragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.data.ExerciseRecordData;
import com.example.survirun.databinding.Fragment1Binding;
import com.example.survirun.server.ServerClient;

import org.eazegraph.lib.models.BarModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment1 extends Fragment {
    Fragment1Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment1Binding.inflate(inflater, container, false);
        View view = binding.getRoot();
        SharedPreferences sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        String token = sf.getString("token", "");
        binding.goalBarGraph.clearChart();
        Call<ExerciseRecordData>call= ServerClient.getServerService().getExerciseRecordData(token);
        call.enqueue(new Callback<ExerciseRecordData>() {
            @Override
            public void onResponse(Call<ExerciseRecordData> call, Response<ExerciseRecordData> response) {
                if(response.isSuccessful()){
                    binding.calorieBarGraph.clearChart();
                    binding.goalBarGraph.clearChart();
                    binding.kmBarGraph.clearChart();
                    binding.exerciseTimeBarGraph.clearChart();
                    for (int i=0;i<response.body().exerciseHistory.size();i++){
                        int calorie=response.body().exerciseHistory.get(i).calorie;
                        double km= response.body().exerciseHistory.get(i).km;
                        String data= response.body().exerciseHistory.get(i).date;
                        int time=response.body().exerciseHistory.get(i).time;
                        Log.d("adsf",time/60+"");
                        binding.kmBarGraph.addBar(new BarModel(data, (float) km, 0xFF56B7F1));
                        binding.calorieBarGraph.addBar(new BarModel(data, calorie, 0xFF56B7F1));
                        binding.exerciseTimeBarGraph.addBar(new BarModel(data,time,0xFF56B7F1));
                    }
                    binding.calorieBarGraph.startAnimation();
                    binding.goalBarGraph.startAnimation();
                    binding.kmBarGraph.startAnimation();
                    binding.exerciseTimeBarGraph.startAnimation();

                }else{
                    Log.d("실패","실패");
                }
            }

            @Override
            public void onFailure(Call<ExerciseRecordData> call, Throwable t) {

            }
        });



        return view;
    }
}