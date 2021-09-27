package com.example.survirun;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ExerciseData;
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
        holder.textView.setText(data.getExName(position) + "   " + data.getHour(position) + "분");
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
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);

        }
    }
}
