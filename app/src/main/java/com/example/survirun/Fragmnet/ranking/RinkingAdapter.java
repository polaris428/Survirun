package com.example.survirun.Fragmnet.ranking;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.survirun.FriendDB;
import com.example.survirun.R;
import com.example.survirun.activity.Friend.FriendActivity;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.ResultData;
import com.example.survirun.data.getUserData;
import com.example.survirun.server.ServerClient;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RinkingAdapter extends  RecyclerView.Adapter<RinkingAdapter.ViewHolder> {
    private List<RankinData> rankinDataList;
    Context context;
    String token;
    public RinkingAdapter(List<RankinData> list) {
        rankinDataList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);

        SharedPreferences sf = context.getSharedPreferences("Login", MODE_PRIVATE);

        token = sf.getString("token", "");
        return new RinkingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Handler handler = new Handler();
        boolean isExpanded = false;
        holder.constraintLayout1.setOnClickListener(v -> {
            if (holder.constraintLayout2.getVisibility() == View.GONE) {
                holder.constraintLayout2.setVisibility(View.VISIBLE);
                ValueAnimator anim = ValueAnimator.ofInt(1, 1200);
                setAnimation(anim, holder.constraintLayout2);

            } else {
                ValueAnimator anim = ValueAnimator.ofInt(1200, 1);
                setAnimation(anim, holder.constraintLayout2);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.constraintLayout2.setVisibility(View.GONE);

                    }
                }, 800);
            }


        });


        holder.rankingTextView.setText(String.valueOf(position+1));
        holder.nameTextView.setText(rankinDataList.get(position).userName);
        holder.scoreTextView.setText(String.valueOf(rankinDataList.get(position).userScore));
        holder.emailTextView.setText(rankinDataList.get(position).userEmail);

        Call<getUserData> call1 = ServerClient.getServerService().getUser(token,rankinDataList.get(position).userEmail);
        call1.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(retrofit2.Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {
                    Log.d(response.body().email,  response.body().score+"");

                    ExerciseHistory exerciseHistory = response.body().exerciseHistory.get( response.body().exerciseHistory.size()-1);
                    holder.kcalTextView.setText(String.valueOf(exerciseHistory.calorie));
                    String hour=String.valueOf(exerciseHistory.time/60);
                    String min=String.valueOf(exerciseHistory.time-Integer.parseInt(hour)*60);
                    if(hour.equals("0")){
                        holder.hourTextView.setVisibility(View.GONE);
                        holder.hurUnitTextView.setVisibility(View.GONE);

                    }else{
                        holder.hourTextView.setText(hour);
                    }
                    if(min.equals("0")&&hour.equals("0")) {
                        holder.minTextView.setText("0");
                    }else if(min.equals("0")){
                        holder.minTextView.setVisibility(View.GONE);
                        holder.minUnitTextView.setVisibility(View.GONE);

                    }else{
                        holder.minTextView.setText(min);


                    }

                    holder.kmTextView.setText(String.format(Locale.getDefault(),"%.2f",exerciseHistory.km));
                    Log.d("ad", response.body().username);

                } else {
                    Log.e("adsf", "실패" + response.code());

                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();
            }
        });
        holder.addFriendButton.setOnClickListener(view -> {
            Call<ResultData> call2 = ServerClient.getServerService().postAddFriend(token, "email", holder.emailTextView.getText().toString());
            call2.enqueue(new Callback<ResultData>() {
                @Override
                public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                    if (response.isSuccessful()) {
                        Call<getUserData> call1 = ServerClient.getServerService().getUser(token,rankinDataList.get(position).userEmail);
                        call1.enqueue(new Callback<getUserData>() {
                            @Override
                            public void onResponse(retrofit2.Call<getUserData> call, Response<getUserData> response) {
                                if (response.isSuccessful()) {
                                    String name=response.body().username;
                                    String email=response.body().email;

                                    Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url", response.body().username);
                                    getProfile.enqueue(new Callback<ImageData>() {
                                        @Override
                                        public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                                            if (response.isSuccessful()) {


                                                String profile = "https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img;
                                                class InsertRunnable implements Runnable {
                                                    @Override
                                                    public void run() {
                                                        FriendRoom friendRoom = new FriendRoom();
                                                        friendRoom.email = email;
                                                        friendRoom.profile = profile;
                                                        friendRoom.name = name;
                                                        FriendDB.getInstance(context).friendDao().insertAll(friendRoom);

                                                    }
                                                }
                                                InsertRunnable insertRunnable = new InsertRunnable();
                                                Thread addThread = new Thread(insertRunnable);
                                                addThread.start();
                                                FriendRoom friendRoom = new FriendRoom();
                                                friendRoom.setEmail(email);
                                                friendRoom.setName(name);
                                                friendRoom.setProfile(profile);
                                                Toast.makeText(context, "친구추가 성공", Toast.LENGTH_LONG).show();




                                            } else {

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ImageData> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });
                                    Toast.makeText(context, "친구추가 성공", Toast.LENGTH_LONG).show();




                                } else {
                                    Log.e("adsf", "실패" + response.code());

                                }
                            }

                            @Override
                            public void onFailure(Call<getUserData> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                        Toast.makeText(context, "친구추가 성공", Toast.LENGTH_LONG).show();

                    } else {
                        //실패시

                    }
                }

                @Override
                public void onFailure(Call<ResultData> call, Throwable t) {

                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return rankinDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankingTextView;

        TextView nameTextView;
        TextView emailTextView;
        TextView scoreTextView;

        TextView kcalTextView;
        TextView hourTextView;
        TextView minTextView;
        TextView hurUnitTextView;
        TextView kmTextView;
        TextView minUnitTextView;


        Button addFriendButton;
        ConstraintLayout constraintLayout1;
        ConstraintLayout constraintLayout2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout1=itemView.findViewById(R.id.card_item1);
            constraintLayout2=itemView.findViewById(R.id.card_item2);

            rankingTextView =itemView.findViewById(R.id.ranking_number_textView);
            nameTextView=itemView.findViewById(R.id.item_ranking_name);
            emailTextView=itemView.findViewById(R.id.item_ranking_email);
            scoreTextView=itemView.findViewById(R.id.best_score);

            kcalTextView=itemView.findViewById(R.id.exercise_kcal_text_view);
            hourTextView=itemView.findViewById(R.id.item_exercise_hour_text_view);
            minTextView=itemView.findViewById(R.id.item_exercise_min_text_view);
            hurUnitTextView=itemView.findViewById(R.id.item_exercise_hur_unit_text_view);
            minUnitTextView=itemView.findViewById(R.id.item_exercise_min_text_view_min_unit_text_view);

            kmTextView=itemView.findViewById(R.id.exercise_km_text_view);

            addFriendButton=itemView.findViewById(R.id.add_friend_button);




        }
    }

    private void setAnimation(ValueAnimator anim, View constraintLayout) {
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            constraintLayout.getLayoutParams().height = value.intValue();
            constraintLayout.requestLayout();
        });
        anim.start();
    }


}
