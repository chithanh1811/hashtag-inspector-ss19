package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TweetHashtagAdapter extends RecyclerView.Adapter<TweetHashtagAdapter.MyViewHolder> {
    private ArrayList<String> hashtagList;
    private Context context;
    private Button mButton;

    public TweetHashtagAdapter (Context context, ArrayList<String> hashtagList) {
        this.context = context;
        this.hashtagList = hashtagList;
    }

    @NonNull
    @Override
    public TweetHashtagAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chipbutton, parent, false);
        return new TweetHashtagAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (TweetHashtagAdapter.MyViewHolder holder, int position) {
        mButton.setText("#" + hashtagList.get(position));
    }

    @Override
    public int getItemCount ( ) {
        if (hashtagList != null)
            return this.hashtagList.size();
        else
            return 0;
    }

    public void update (ArrayList<String> newhashtagList) {
        if (newhashtagList != null) {
            hashtagList = newhashtagList;
            notifyDataSetChanged();
            System.out.println(this.hashtagList.size());
        } else {
            System.out.println("NULL");
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyViewHolder (View v) {
            super(v);
            mButton = itemView.findViewById(R.id.hashtag);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    String query = mButton.getText().toString();
                    Intent intent = new Intent(context, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", query);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((SearchResultsActivity)context).finish();
                }
            });
        }

        @Override
        public void onClick (View v) {

        }


    }
}
