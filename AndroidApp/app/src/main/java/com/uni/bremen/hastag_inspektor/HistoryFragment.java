package com.uni.bremen.hastag_inspektor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, null);

        // History RecyclerView
        RecyclerView historyRecyclerView = view.findViewById(R.id.history_recyclerView_historyFragment);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        historyRecyclerView.setAdapter(((MainActivity) getActivity()).searchQueryAdapter);

        // Clear History Button
        FloatingActionButton clearHistoryButton = view.findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(v -> ((MainActivity) getActivity()).clearHistory());
        return view;
    }
}

