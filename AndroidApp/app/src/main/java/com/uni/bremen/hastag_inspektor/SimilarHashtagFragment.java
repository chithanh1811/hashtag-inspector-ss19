package com.uni.bremen.hastag_inspektor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimilarHashtagFragment extends Fragment {

    private RecyclerView similarHashtagRecyclerView;
    private RecyclerView.LayoutManager similarHashtagLayoutManager;
    private RecyclerView.Adapter similarHashtagAdapter;

    public SimilarHashtagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similarhashtag, container, false);
        similarHashtagRecyclerView = view.findViewById(R.id.related_hashtag_recyclerView);
        similarHashtagLayoutManager = new LinearLayoutManager(this.getActivity());
        similarHashtagRecyclerView.setHasFixedSize(true);
        similarHashtagAdapter = new MyAdapter(MainActivity.getOccurrencesArrayList());

        similarHashtagRecyclerView.setLayoutManager(similarHashtagLayoutManager);
        similarHashtagRecyclerView.setAdapter(similarHashtagAdapter);

        return view;
    }
}