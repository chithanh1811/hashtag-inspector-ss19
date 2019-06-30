package com.uni.bremen.hastag_inspektor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TweetFragment extends Fragment {
    private RecyclerView tweetRecyclerView;
    private RecyclerView.LayoutManager tweetLayoutManager;
    private RecyclerView.Adapter tweetAdapter;
    
    public TweetFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);

        tweetRecyclerView = view.findViewById(R.id.tweet_recyclerView);
        tweetLayoutManager = new LinearLayoutManager(this.getActivity());
        tweetRecyclerView.setHasFixedSize(true);
        tweetAdapter = new TweetAdapter(MainActivity.getTweets());
        tweetRecyclerView.setLayoutManager(tweetLayoutManager);
        tweetRecyclerView.setAdapter(tweetAdapter);
        return view;
    }

}