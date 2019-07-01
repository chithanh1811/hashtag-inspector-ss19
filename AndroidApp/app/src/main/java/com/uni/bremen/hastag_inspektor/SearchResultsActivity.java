package com.uni.bremen.hastag_inspektor;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView sentiment;
    public static final String QUERY_ARG = "query";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        System.out.println("SearchResultsActivity");

        setTitle(intent.getStringExtra("title"));
        setContentView(R.layout.activity_search_results);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run ( ) {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        ArrayList<Tweet> tweets = MainActivity.getTweets();
        float sum = 0;
        for (Tweet tweet : tweets) {
            if (tweet.getSentiment() != null && !tweet.getSentiment().isEmpty()) {
                sum += Float.parseFloat(tweet.getSentiment());
            }
            float avg = sum / tweets.size();
            sentiment = findViewById(R.id.sentimentBar);
            if (avg == 0){
                sentiment.setText("N/A");
            }
            else if (avg > 0.5) {
                sentiment.setText("Positive (" + avg + ")");
                sentiment.setBackgroundColor(getResources().getColor(R.color.twitterBlue));
            } else if (avg == 0.5) {
                sentiment.setText("Neutral (" + avg + ")");
            } else {
                sentiment.setText("Negative (" + avg + ")");
                sentiment.setBackgroundColor(getResources().getColor(R.color.colorWarning));
            }
        }


        getSupportActionBar().setElevation(0);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
