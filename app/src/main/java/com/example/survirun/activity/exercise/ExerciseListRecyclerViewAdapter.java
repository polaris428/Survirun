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

    public ExerciseListRecyclerViewAdapter(ArrayList<ExerciseData> list) {
        items = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseData data = items.get(position);
        holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + data.getHour(position) + "분");
        holder.calorieTextView.setText(data.getCalorie(position)+"");
        holder.timeTextView.setText(data.getHour(position)+"");
        holder.kmTextView.setText(data.getKm(position)+"");

        Handler handler = new Handler();
        holder.expandImageButton.setOnClickListener(view -> {
            if(holder.cardLayout.getVisibility()==View.GONE){
                ValueAnimator anim = ValueAnimator.ofInt(230,650);
                setAnimation(anim, holder.cardView2);
                holder.expandImageButton.setImageResource(R.drawable.ic_upblack);
                holder.cardLayout.setVisibility(View.VISIBLE);
            }else {
                ValueAnimator anim = ValueAnimator.ofInt(650,230);
                setAnimation(anim, holder.cardView2);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.expandImageButton.setImageResource(R.drawable.ic_downblack);
                        holder.cardLayout.setVisibility(View.GONE);
                    }
                },800);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ExercisePreparationActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout, cardLayout;
        TextView exerciseTitleTextview;
        TextView calorieTextView;
        TextView timeTextView;
        TextView kmTextView;
        ImageButton expandImageButton;
        CardView cardView1, cardView2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseTitleTextview = itemView.findViewById(R.id.exercise_title_textview);
            calorieTextView=itemView.findViewById(R.id.item_calorie_text_view);
            timeTextView=itemView.findViewById(R.id.item_time_text_view);
            kmTextView=itemView.findViewById(R.id.item_km_text_view);
            expandImageButton=itemView.findViewById(R.id.expand_image_button);
            layout = itemView.findViewById(R.id.layout);
            cardLayout = itemView.findViewById(R.id.card_layout2);
            cardView1 = itemView.findViewById(R.id.card_view1);
            cardView2 = itemView.findViewById(R.id.card_view2);
        }
    }

    private void setAnimation(ValueAnimator anim, CardView cardView){
        anim.setDuration(800);
        anim.addUpdateListener(animation -> {
            Integer value = (Integer) animation.getAnimatedValue();
            cardView.getLayoutParams().height = value.intValue();
            cardView.requestLayout();
        });
        anim.start();
    }
}
