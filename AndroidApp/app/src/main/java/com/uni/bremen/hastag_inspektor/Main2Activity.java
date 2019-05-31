package com.uni.bremen.hastag_inspektor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


//        ArrayList<HashtagAndOccurences> hashtagAndOccurencesArrayList = new ArrayList<>(MainActivity.getOccurrences());
//
//        System.out.println("In activity two we are reading the hashtag and occurrences: ");
//        for (HashtagAndOccurences hashtagAndOccurences : hashtagAndOccurencesArrayList) {
//            System.out.println("Hashtag and occurrences are in activity 2 are as follow" + hashtagAndOccurences.getHashtagName() + " || " + hashtagAndOccurences.getNumberOfOccurrences());
//
//        }

        mRecyclerView = findViewById(R.id.listOfPopularHashtagRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(MainActivity.getOccurrences());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



    }
}
