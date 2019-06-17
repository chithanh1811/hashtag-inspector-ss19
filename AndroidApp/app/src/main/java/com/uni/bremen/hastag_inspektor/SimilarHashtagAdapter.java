package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;


public class SimilarHashtagAdapter extends RecyclerView.Adapter<SimilarHashtagAdapter.MyViewHolder> {
    private ArrayList<HashtagAndOccurences> hashtagAndOccurrences;
    public SparseBooleanArray itemStateArray = new SparseBooleanArray();
    private ArrayList<String> clickedItems = new ArrayList<String>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(ArrayList<String> clickedItems);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        this.listener = listener;
    }

    public SimilarHashtagAdapter(ArrayList<HashtagAndOccurences> hashtagAndOccurrences) {
        this.hashtagAndOccurrences = hashtagAndOccurrences;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.row;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.hashtagAndOccurrences.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckedTextView mCheckedTextView;
        TextView mTextViewHashtagOccurrences;

        MyViewHolder(View v) {
            super(v);
            mCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.checked_text_view);
            mTextViewHashtagOccurrences = v.findViewById(R.id.hashTagOccurrences);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                mCheckedTextView.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                clickedItems.add(hashtagAndOccurrences.get(adapterPosition).getHashtagName().toString());
            } else {
                mCheckedTextView.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                clickedItems.add(hashtagAndOccurrences.remove(adapterPosition).getHashtagName().toString());
            }
            listener.onClick(clickedItems);
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                mCheckedTextView.setChecked(false);
            } else {
                mCheckedTextView.setChecked(true);
            }
            mCheckedTextView.setText(hashtagAndOccurrences.get(position).getHashtagName());
            mTextViewHashtagOccurrences.setText(String.valueOf(hashtagAndOccurrences.get(position).getNumberOfOccurrences()));
        }

    }
}
