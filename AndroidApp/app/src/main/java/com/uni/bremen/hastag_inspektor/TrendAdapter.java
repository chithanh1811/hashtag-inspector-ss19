package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.MyViewHolder> {
    private ArrayList<String> trendList;
    private TrendAdapter.OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onClick (ArrayList<String> clickedItems);
    }

    public void setOnItemClickListener (TrendAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public TrendAdapter (Context context, ArrayList<String> trendList) {
        this.context = context;
        this.trendList = trendList;
    }

    @NonNull
    @Override
    public TrendAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        int layoutForItem = R.layout.items;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new TrendAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder (TrendAdapter.MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount ( ) {
        if (trendList != null)
            return this.trendList.size();
        else
            return 0;
    }

    public void update (ArrayList<String> newTrendList) {
        if (newTrendList != null) {
            trendList = newTrendList;
            notifyDataSetChanged();
            System.out.println(this.trendList.size());
        } else {
            System.out.println("NULL");
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;

        MyViewHolder (View v) {
            super(v);
            mTextView = v.findViewById(R.id.search_query);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String query = mTextView.getText().toString();
                    ((MainActivity) context).startSearch(query);
                    Intent myIntent = new Intent((MainActivity) context, SearchResultsActivity.class);
                    myIntent.putExtra("title", query);
                    ((MainActivity) context).startActivity(myIntent);
                }
            });
        }

        @Override
        public void onClick (View v) {

        }

        void bind (int position) {
            mTextView.setText(trendList.get(position));
        }

    }
}
