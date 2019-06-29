package com.uni.bremen.hastag_inspektor;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, null);

        // History
        // TODO @Thanh Populating a ChipGroup instead of a List
        RecyclerView historyRecyclerView  = view.findViewById(R.id.history_recyclerView_exploreFragment);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        historyRecyclerView.setAdapter(((MainActivity)getActivity()).searchQueryAdapter);

        // @TODO Thanh: Trending Hashtags
        RecyclerView trendsRecyclerView = view.findViewById(R.id.trending_recyclerView_exploreFragment);
        trendsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        trendsRecyclerView.setAdapter(((MainActivity)getActivity()).trendAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}