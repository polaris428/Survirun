package com.example.survirun.activity.exercise;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ExerciseData;
import com.example.survirun.R;
import com.example.survirun.activity.ExercisePreparationActivity;

import java.util.ArrayList;

public class ExerciseListRecyclerViewAdapter extends RecyclerView.Adapter<ExerciseListRecyclerViewAdapter.ViewHolder> {
    public ArrayList<ExerciseData> items;

    public ExerciseListRecyclerViewAdapter(ArrayList<ExerciseData> list) {
        items = list;
    }

    @NonNull
    @Override
    public ExerciseListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        ExerciseListRecyclerViewAdapter.ViewHolder viewHolder = new ExerciseListRecyclerViewAdapter.ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseData data = items.get(position);
        Animation animationDown = AnimationUtils.loadAnimation(holder.cardView.getContext(),R.anim.sliding_down);
        Animation animationUp = AnimationUtils.loadAnimation(holder.cardView.getContext(),R.anim.sliding_up);
        Handler handler = new Handler();
        holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + data.getHour(position) + "분");
        holder.calorieTextView.setText(data.getCalorie(position)+"");
        holder.timeTextView.setText(data.getHour(position)+"");
        holder.kmTextView.setText(data.getKm(position)+"");

        holder.expandImageButton.setOnClickListener(view -> {
            if(holder.constraintLayout.getVisibility()==View.GONE){
                holder.constraintLayout.startAnimation(animationDown);
                holder.expandImageButton.setImageResource(R.drawable.ic_upblack);
                holder.constraintLayout.setVisibility(View.VISIBLE);
            }else {
                holder.constraintLayout.startAnimation(animationUp);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.expandImageButton.setImageResource(R.drawable.ic_downblack);
                        holder.constraintLayout.setVisibility(View.GONE);

                    }
                },1000);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ExercisePreparationActivity.class);
                intent.putExtra("title",holder.exerciseTitleTextview.getText());
                intent.putExtra("calorie",holder.calorieTextView.getText());
                intent.putExtra("km",holder.kmTextView.getText());
                intent.putExtra("time",holder.timeTextView.getText());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView exerciseTitleTextview;
        TextView calorieTextView;
        TextView timeTextView;
        TextView kmTextView;
        ImageButton expandImageButton;
        CardView  cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.constraint);
            exerciseTitleTextview = itemView.findViewById(R.id.exercise_title_textview);
            calorieTextView=itemView.findViewById(R.id.item_calorie_text_view);
            timeTextView=itemView.findViewById(R.id.item_time_text_view);
            kmTextView=itemView.findViewById(R.id.item_km_text_view);
            expandImageButton=itemView.findViewById(R.id.expand_image_button);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
