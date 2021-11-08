package com.example.survirun.Fragmnet.Friend;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ModelLoader;
import com.example.survirun.R;
import com.example.survirun.data.ExerciseHistory;
import com.example.survirun.data.Friends;
import com.example.survirun.data.ImageData;
import com.example.survirun.data.getUserData;
import com.example.survirun.server.ServerClient;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<Friends> mData ;
    Context context;
    String token;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ConstraintLayout constraintLayout1;
        ConstraintLayout constraintLayout2;
        ImageView profileImageview;
        TextView nameTextView;
        TextView exerciseTextview;
        Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_friend_name);
            constraintLayout1=itemView.findViewById(R.id.constraint_layout);
            constraintLayout2=itemView.findViewById(R.id.card_item2);
            nameTextView=itemView.findViewById(R.id.username_textview);
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

        holder.textView.setText(mData.get(position).email);
        holder.detailButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, FriendInformationActivity.class);
            intent.putExtra("list", "값");
            context.startActivity(intent);
        });
        SharedPreferences sf = context.getSharedPreferences("Login", MODE_PRIVATE);

        token = sf.getString("token", "");
        Call<getUserData> call1= ServerClient.getServerService().getUser(token,holder.textView.getText().toString());
        call1.enqueue(new Callback<getUserData>() {
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
                                        .error(R.drawable.ic_profile)
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


}
