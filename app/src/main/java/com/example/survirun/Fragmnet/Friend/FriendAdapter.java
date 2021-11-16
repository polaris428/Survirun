package com.example.survirun.Fragmnet.Friend;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.FriendRoom;
import com.example.survirun.data.getUserData;
import com.example.survirun.server.ServerClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<FriendRoom> friendRoomList;

    Context context;
    String token;
    String profile;

    public FriendAdapter(List<FriendRoom> list) {
        friendRoomList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_friend, parent, false);
        FriendAdapter.ViewHolder viewHolder = new FriendAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences sf = context.getSharedPreferences("Login", MODE_PRIVATE);

        token = sf.getString("token", "");
        FriendRoom item = friendRoomList.get(position);
        holder.email.setText(item.email);
        holder.name.setText(item.name);
        Glide.with(context).load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + item.profile)
                .error(R.drawable.userdefaultprofile)
                .circleCrop()
                .into(holder.profile);

        Log.d("adsf", item.profile);
        holder.detailButton.setOnClickListener(v -> {

            Intent intent = new Intent(context, FriendInformationActivity.class);
            intent.putExtra("userName", holder.name.getText().toString());
            intent.putExtra("userEmail", holder.email.getText().toString());
            intent.putExtra("profile", item.profile);

            context.startActivity(intent);

        });
        Call<getUserData> call1 = ServerClient.getServerService().getUser(token, item.email);
        call1.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if (response.isSuccessful()) {
                    Log.d(response.body().email,  response.body().score+"");

                    ExerciseHistory exerciseHistory = response.body().exerciseHistory.get(0);
                    holder.exerciseKcalTextview.setText(String.valueOf(exerciseHistory.calorie));
                    holder.exerciseTimeTextView.setText(String.valueOf(exerciseHistory.time));
                    holder.exerciseKmTextView.setText(String.valueOf(exerciseHistory.km));
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


    }

    @Override
    public int getItemCount() {
        return friendRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        ImageView profile;
        TextView exerciseKcalTextview;
        TextView exerciseTimeTextView;
        TextView exerciseKmTextView;
        ConstraintLayout constraintLayout1;
        ConstraintLayout constraintLayout2;
        Button detailButton;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_friend_name);
            email = itemView.findViewById(R.id.item_friend_email);
            profile = itemView.findViewById(R.id.profile_imageview);
            constraintLayout1 = itemView.findViewById(R.id.constraint_layout);
            constraintLayout2 = itemView.findViewById(R.id.card_item2);
            exerciseKcalTextview = itemView.findViewById(R.id.exercise_kcal_text_view);
            exerciseTimeTextView = itemView.findViewById(R.id.exercise_time_text_view);
            exerciseKmTextView = itemView.findViewById(R.id.exercise_km_text_view);
            detailButton = itemView.findViewById(R.id.detail_button);
            context = itemView.getContext();


            Handler handler = new Handler();
            boolean isExpanded = false;
            constraintLayout1.setOnClickListener(v -> {
                if (constraintLayout2.getVisibility() == View.GONE) {
                    constraintLayout2.setVisibility(View.VISIBLE);
                    ValueAnimator anim = ValueAnimator.ofInt(1, 1200);
                    setAnimation(anim, constraintLayout2);

                } else {
                    ValueAnimator anim = ValueAnimator.ofInt(1200, 1);
                    setAnimation(anim, constraintLayout2);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            constraintLayout2.setVisibility(View.GONE);

                        }
                    }, 800);
                }


            });

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


    /*public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ConstraintLayout constraintLayout1;
        ConstraintLayout constraintLayout2;
        ImageView profileImageview;
        TextView nameTextView;
        TextView exerciseTextview;
        Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_friend_email);
            constraintLayout1=itemView.findViewById(R.id.constraint_layout);
            constraintLayout2=itemView.findViewById(R.id.card_item2);
            nameTextView=itemView.findViewById(R.id.item_friend_name);
            profileImageview=itemView.findViewById(R.id.profile_imageview);
            exerciseTextview=itemView.findViewById(R.id.exercise_textview);
            detailButton=itemView.findViewById(R.id.detail_button);
             context=itemView.getContext();

             constraintLayout1.setOnClickListener(v -> {

                if (constraintLayout2.getVisibility() == View.GONE){
                    constraintLayout2.setVisibility(View.VISIBLE);

                }else{
                    constraintLayout2.setVisibility(View.GONE);
                }


            });
        }
    }

    FriendAdapter(List<Friends> list) {
        mData = list;

    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_friend, parent, false);
        FriendAdapter.ViewHolder vh = new FriendAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        SharedPreferences sf = context.getSharedPreferences("Login", MODE_PRIVATE);

        token = sf.getString("token", "");
        friendName=mData.get(position).email;
        holder.textView.setText(friendName);
        Call<getUserData>call1=ServerClient.getServerService().getUser(token,friendName);
        call1.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if(response.isSuccessful()){

                    ExerciseHistory exerciseHistory=response.body().exerciseHistory.get(0);
                    holder.nameTextView.setText(response.body().username);
                    Log.d("ad",response.body().username);

                }else{
                    Log.e("adsf","실패"+ response.code());

                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();
            }
        });

        holder.detailButton.setOnClickListener(v -> {



//            Intent intent = new Intent(context, FriendInformationActivity.class);
//            intent.putExtra("list", "값");
//            context.startActivity(intent);
        });

        Call<getUserData> call2= ServerClient.getServerService().getUser(token,holder.textView.getText().toString());
        call2.enqueue(new Callback<getUserData>() {
            @Override
            public void onResponse(Call<getUserData> call, Response<getUserData> response) {
                if(response.isSuccessful()){


                    ExerciseHistory exerciseHistory= Objects.requireNonNull(response.body()).exerciseHistory.get(0);
                    holder.exerciseTextview.setText(exerciseHistory.calorie+"칼로리"+exerciseHistory.km+"킬로미터"+ exerciseHistory.time+"운동시간");
                    Log.d("adsf",exerciseHistory.km+"");
                    Call<ImageData> getProfile = ServerClient.getServerService().getSuchProfile(token, "username", "url",response.body().username);
                    getProfile.enqueue(new Callback<ImageData>() {
                        @Override
                        public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                            if (response.isSuccessful()) {
                                Log.d("d",response.body().img);
                                Glide.with(context)
                                        .load("https://dicon21.2tle.io/api/v1/image?reqType=profile&id=" + response.body().img)
                                        .error(R.drawable.userdefaultprofile)
                                        .circleCrop()
                                        .into(holder.profileImageview);
                            }
                        }

                        @Override
                        public void onFailure(Call<ImageData> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }else{
                    Log.d("adsf","실패");
                }
            }

            @Override
            public void onFailure(Call<getUserData> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

*/
}
