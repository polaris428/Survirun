package com.example.survirun.Fragmnet.ranking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.survirun.Fragmnet.Friend.FriendAdapter;
import com.example.survirun.Fragmnet.excise.ExerciseListRecyclerViewAdapter;
import com.example.survirun.R;
import com.example.survirun.loom.ExerciseData;

import java.util.ArrayList;
import java.util.List;

public class RinkingAdapter extends  RecyclerView.Adapter<RinkingAdapter.ViewHolder> {
    private List<RankinData> rankinDataList;
    public RinkingAdapter(List<RankinData> list) {
        rankinDataList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);


        return new RinkingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(rankinDataList.get(position).userName);
    }

    @Override
    public int getItemCount() {
        return rankinDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.text_view);
        }
    }


}
