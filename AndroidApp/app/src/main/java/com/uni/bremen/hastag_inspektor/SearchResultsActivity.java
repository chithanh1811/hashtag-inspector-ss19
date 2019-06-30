package com.uni.bremen.hastag_inspektor;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SearchResultsActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static final String QUERY_ARG = "query";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setTitle(intent.getStringExtra("title"));
        setContentView(R.layout.activity_search_results);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        getSupportActionBar().setElevation(0);
    }
    @Override
    public boolean onSupportNavigateUp(){
        getIntent().replaceExtras(new Bundle());
        System.out.println("Intent cleared");
        finish();
        return true;
    }

}
