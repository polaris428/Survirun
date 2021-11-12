package com.example.survirun.activity.exercise;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteme_xercise, parent, false);
        ExerciseListRecyclerViewAdapter.ViewHolder viewHolder = new ExerciseListRecyclerViewAdapter.ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseData data = items.get(position);
        holder.exerciseTitleTextview.setText(data.getExName(position) + "   " + data.getHour(position) + "분");
        holder.calorieTextView.setText(data.getCalorie(position)+"");
        holder.timeTextView.setText(data.getHour(position)+"");
        holder.kmTextView.setText(data.getKm(position)+"");
        holder.expandImageButton.setOnClickListener(view -> {
            if(holder.constraintLayout.getVisibility()==View.GONE){
                holder.expandImageButton.setImageResource(R.drawable.ic_upblack);
                holder.constraintLayout.setVisibility(View.VISIBLE);
            }else {
                holder.expandImageButton.setImageResource(R.drawable.ic_downblack);
                holder.constraintLayout.setVisibility(View.GONE);

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
        ConstraintLayout constraintLayout;
        TextView exerciseTitleTextview;
        TextView calorieTextView;
        TextView timeTextView;
        TextView kmTextView;
        ImageButton expandImageButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.constraint);
            exerciseTitleTextview = itemView.findViewById(R.id.exercise_title_textview);
            calorieTextView=itemView.findViewById(R.id.item_calorie_text_view);
            timeTextView=itemView.findViewById(R.id.item_time_text_view);
            kmTextView=itemView.findViewById(R.id.item_km_text_view);
            expandImageButton=itemView.findViewById(R.id.expand_image_button);

        }
    }
}
