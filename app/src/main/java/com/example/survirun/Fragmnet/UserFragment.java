package com.example.survirun.Fragmnet;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.data.ExerciseData;

import com.example.survirun.databinding.FragmentUserBinding;
import com.example.survirun.server.ServerClient;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    FragmentUserBinding binding;
    int progress = 0;
    int goalCalorie;
    int goalTime;
    int goalKm;

    String token;
    String name;
    SharedPreferences goal;
    SharedPreferences sf;

    SharedPreferences.Editor editor;
    String emile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
       // binding.drawerLayout.setDrawerListener(listener);
        goal = getContext().getSharedPreferences("goal", MODE_PRIVATE);
        goalCalorie = goal.getInt("calorie", 400);
        goalTime = goal.getInt("time", 60);
        goalKm = goal.getInt("km", 5);
        sf = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        editor=sf.edit();
        token = sf.getString("token", "");

        name=sf.getString("name","");
        emile=sf.getString("email","");


        binding.userPageButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), UserPageActivity.class);
//            startActivity(intent);

            binding.drawerLayout.openDrawer(binding.navView);
        });


        Call<ExerciseData> call = ServerClient.getServerService().getExercise(token);
        call.enqueue(new Callback<ExerciseData>() {
            @Override
            public void onResponse(Call<ExerciseData> call, Response<ExerciseData> response) {
                if (response.isSuccessful()) {
                    binding.dateTextview.setText(response.body().date);
                    binding.calorieTextview.setText(response.body().calorie + "");
                    binding.timeTextview.setText(response.body().time + "");
                    binding.kmTextview.setText(String.format("%.2f",response.body().km));
                    editor.putString("data",response.body().date);

                    if (goalCalorie / 2 < response.body().calorie) {
                        progress = progress + 25;
                        binding.calorieCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalTime / 2 < response.body().time) {
                        progress = progress + 25;
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.YELLOW);
                    }
                    if (goalKm / 2 < response.body().km) {
                        progress = progress + 25;
                        binding.kmCardView.setCardBackgroundColor(Color.YELLOW);
                    }

                    if (goalCalorie <= response.body().calorie) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.calorieCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalTime <= response.body().time) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.exerciseTimeCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (goalKm <= response.body().time) {
                        progress = progress + 8;
                        binding.arcProgress.setProgress(progress);
                        binding.kmCardView.setCardBackgroundColor(Color.GREEN);
                    }
                    if (progress >= 99) {
                        progress = 100;
                        binding.arcProgress.setProgress(progress);
                    }
                }
            }

            @Override
            public void onFailure(Call<ExerciseData> call, Throwable t) {

            }
        });



        return view;
    }/*
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            /*ActivityNavigateDrawerBinding nbinding ;
            nbinding= ActivityNavigateDrawerBinding.bind(drawerView);



            nbinding.nameTextView.setText(name);
            nbinding.idTextView.setText(emile);

            Call<ImageData> getProfile = ServerClient.getServerService().getProfile(token, "self", "url");
            getProfile.enqueue(new Callback<ImageData>() {
                @Override
                public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                    if (response.isSuccessful()) {
                        Log.d("adf", response.body().img);
                        Glide.with(getContext())
                                .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                                .circleCrop()
                                .error(R.drawable.ic_profile)
                                .into(nbinding.profileImageview);
                    }
                }

                @Override
                public void onFailure(Call<ImageData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());;
            nbinding.logoutButton.setOnClickListener(v -> builder.setTitle(R.string.logout).setMessage(R.string.logout_user).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putString("email", "");
                    editor.putString("pwe", "");
                    editor.putString("token", "");
                    editor.putString("name", "");
                    editor.commit();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.no, null).show());

            nbinding.goalEditButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), UserGoalActivity.class);
                startActivity(intent);
            });

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };
*/
}