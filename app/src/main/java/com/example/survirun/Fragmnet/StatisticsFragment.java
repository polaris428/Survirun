package com.example.survirun.Fragmnet;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.survirun.R;
import com.example.survirun.Fragmnet.inFragment.ViewPagerAdapter;
import com.example.survirun.data.ExerciseRecordData;
import com.example.survirun.databinding.FragmentStatisticsBinding;
import com.example.survirun.server.ServerClient;
import com.google.android.material.tabs.TabLayoutMediator;

import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {
    FragmentStatisticsBinding binding;
    private final List<String> titles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        titles.add(getString(R.string.total_exercise));
//        titles.add(getString(R.string.goal));
//        titles.add(getString(R.string.route));
//        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
//
//        binding.viewPager.setAdapter(adapter);
//
//        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);

        SharedPreferences sf = getContext().getSharedPreferences("Login", getContext().MODE_PRIVATE);
        String token = sf.getString("token", "");
        binding.image.startAnimation(animation);
        binding.findRecord.setVisibility(View.VISIBLE);
        Call<ExerciseRecordData> call= ServerClient.getServerService().getExerciseRecordData(token);
        call.enqueue(new Callback<ExerciseRecordData>() {
            @Override
            public void onResponse(Call<ExerciseRecordData> call, Response<ExerciseRecordData> response) {
                if(response.isSuccessful()){
                    binding.calorieBarGraph.clearChart();;
                    binding.kmBarGraph.clearChart();
                    binding.exerciseTimeBarGraph.clearChart();
                    if(response.body().exerciseHistory.size()==1&&response.body().exerciseHistory.get(0).time==0){
                        binding.noRecord.setVisibility(View.VISIBLE);
                        binding.findRecord.setVisibility(View.GONE);
                        binding.ExerciseGraph.setVisibility(View.GONE);

                    }else{
                        binding.ExerciseGraph.setVisibility(View.VISIBLE);
                        binding.ExerciseMessage.setVisibility(View.GONE);

                        for (int i=0;i<response.body().exerciseHistory.size();i++){
                            int calorie=response.body().exerciseHistory.get(i).calorie;
                            double km= response.body().exerciseHistory.get(i).km;
                            String data= response.body().exerciseHistory.get(i).date;
                            int time=response.body().exerciseHistory.get(i).time;


                            binding.kmBarGraph.addBar(new BarModel(data, (int) km, 0xFFcc4444));
                            binding.calorieBarGraph.addBar(new BarModel(data, calorie, 0xFFcc4444));
                            binding.exerciseTimeBarGraph.addBar(new BarModel(data,time/60,0xFFcc4444));
                        }
                        binding.calorieBarGraph.startAnimation();
                        binding.kmBarGraph.startAnimation();
                        binding.exerciseTimeBarGraph.startAnimation();
                    }


                }else{
                    Toast.makeText(getContext(), "서버오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExerciseRecordData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "서버오류", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}