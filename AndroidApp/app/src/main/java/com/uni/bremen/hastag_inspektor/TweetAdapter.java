package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.service.autofill.TextValueSanitizer;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.MyViewHolder> {
    public ArrayList<Tweet> tweets;
    public Context context;

    public TweetAdapter(ArrayList<Tweet> tweets) {
        this.tweets = new ArrayList<>();
        for (Tweet t : tweets)
            this.tweets.add(t);
    }

    @NonNull
    @Override
    public TweetAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        int layoutForItem = R.layout.tweet;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
        return new TweetAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TweetAdapter.MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.tweets.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mUsername;
        TextView mTimestamp;
        TextView mContent;
        ImageView mAvatar;
        String mLink;
        RecyclerView hashtagRecyclerView;
        TextView mSentiment;
        View v;

        MyViewHolder(View v) {
            super(v);
            this.v = v;
            mUsername = (TextView) v.findViewById(R.id.tweet_username);
            mTimestamp = (TextView) v.findViewById(R.id.tweet_timestamp);
            mContent = (TextView) v.findViewById(R.id.tweet_content);
            mAvatar = (ImageView) v.findViewById(R.id.tweet_avatar);
            hashtagRecyclerView = v.findViewById(R.id.tweethashtag_recyclerView);
            mSentiment = (TextView) v.findViewById(R.id.sentiment);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLink));
                    context.startActivity(browserIntent);
                }
            });
        }

        @Override
        public void onClick(View v) {
        }

        void bind(int position) {
            mUsername.setText(tweets.get(position).getAccount());
            mTimestamp.setText(tweets.get(position).getTimestamp().toString());
            mContent.setText(tweets.get(position).getContent());
            Picasso.get().load(tweets.get(position).getAvatar()).transform(new CircleTransform()).into(mAvatar);
            mLink = tweets.get(position).getLink();
            hashtagRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            hashtagRecyclerView.setAdapter(new TweetHashtagAdapter(context, tweets.get(position).getHashtagList()));
            if (tweets.get(position).getSentiment() != null && !tweets.get(position).getSentiment().isEmpty()) {
                if (Float.parseFloat(tweets.get(position).getSentiment()) >= 0.55)
                    mSentiment.setText("Positive (" + tweets.get(position).getSentiment() + ")");
                else if (Float.parseFloat(tweets.get(position).getSentiment()) >= 0.5 && Float.parseFloat(tweets.get(position).getSentiment()) < 0.55)
                    mSentiment.setText("Neutral (" + tweets.get(position).getSentiment() + ")");
                else
                    mSentiment.setText("Controversial (" + tweets.get(position).getSentiment() + ")");
            }
            else {
                mSentiment.setText("N/A");
            }

        }

    }
}
