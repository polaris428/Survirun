package com.example.survirun.activity.exercise;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.util.SparseBooleanArray;
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
    String min, hr;
    String hour, minute;
    public ExerciseListRecyclerViewAdapter(ArrayList<ExerciseData> list) {
        items = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        min = itemView.getContext().getString(R.string.min);
        hr = itemView.getContext().getString(R.string.hour);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseData data = items.get(position);
        if(data.getHour(position)>60){
            hour = String.valueOf(data.getHour(position)/60);
            minute = String.valueOf(data.getHour(position)%60);
            holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + hour + hr + " " + minute + min);
        }
        else if(data.getHour(position)==60){
            hour = String.valueOf(data.getHour(position)/60);
            holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + hour + hr);
        }
        else{
            holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + data.getHour(position) + min);
        }

        holder.calorieTextView.setText(data.getCalorie(position)+"");
        holder.timeTextView.setText(data.getHour(position)+"");
        holder.kmTextView.setText(data.getKm(position)+"");

        Handler handler = new Handler();
        holder.expandImageButton.setOnClickListener(view -> {
            if(holder.constraintLayout.getVisibility()==View.GONE){
                ValueAnimator anim = ValueAnimator.ofInt(1, 450);
                setAnimation(anim, holder.constraintLayout);
                holder.expandImageButton.setImageResource(R.drawable.ic_upblack);
                holder.constraintLayout.setVisibility(View.VISIBLE);
            }else {
                ValueAnimator anim = ValueAnimator.ofInt(450, 1);
                setAnimation(anim, holder.constraintLayout);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.expandImageButton.setImageResource(R.drawable.ic_downblack);
                        holder.constraintLayout.setVisibility(View.GONE);
                    }
                },800);

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseTitleTextview = itemView.findViewById(R.id.exercise_title_textview);
            calorieTextView=itemView.findViewById(R.id.item_calorie_text_view);
            timeTextView=itemView.findViewById(R.id.item_time_text_view);
            kmTextView=itemView.findViewById(R.id.item_km_text_view);
            expandImageButton=itemView.findViewById(R.id.expand_image_button);
            constraintLayout = itemView.findViewById(R.id.constraint);
        }
    }

    private void setAnimation(ValueAnimator anim, ConstraintLayout constraintLayout){
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            constraintLayout.getLayoutParams().height = value.intValue();
            constraintLayout.requestLayout();
        });
        anim.start();
    }
}
