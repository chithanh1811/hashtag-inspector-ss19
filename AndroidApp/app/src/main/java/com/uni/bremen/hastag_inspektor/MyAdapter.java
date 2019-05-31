package com.uni.bremen.hastag_inspektor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<HashtagAndOccurences> hashtagAndOccurrences;


    public MyAdapter(ArrayList<HashtagAndOccurences> hashtagAndOccurrences) {
        this.hashtagAndOccurrences = hashtagAndOccurrences;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        HashtagAndOccurences hashtagAndOccurences = this.hashtagAndOccurrences.get(i);

        myViewHolder.mTextViewHashtagTitle.setText(hashtagAndOccurences.getHashtagName());
        myViewHolder.mTextViewHashtagOccurrences.setText(String.valueOf(hashtagAndOccurences.getNumberOfOccurrences()));
    }

    @Override
    public int getItemCount() {
        return this.hashtagAndOccurrences.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewHashtagTitle;
        public TextView mTextViewHashtagOccurrences;

        public MyViewHolder(View v) {
            super(v);
            mTextViewHashtagTitle = v.findViewById(R.id.hashTagTitle);
            mTextViewHashtagOccurrences = v.findViewById(R.id.hashTagOccurrences);

        }
    }
}
