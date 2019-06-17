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
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SimilarHashtagFragment extends Fragment {

    private RecyclerView similarHashtagRecyclerView;
    private RecyclerView.LayoutManager similarHashtagLayoutManager;
    private RecyclerView.Adapter similarHashtagAdapter;
    private ArrayList<String> clickedItem = new ArrayList<String>();

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
        similarHashtagAdapter = new SimilarHashtagAdapter(MainActivity.getOccurrencesArrayList());
        ((SimilarHashtagAdapter) similarHashtagAdapter).setOnItemClickListener(new SimilarHashtagAdapter.OnItemClickListener() {
            @Override
            public void onClick (ArrayList<String> clickedItems) {
                clickedItem = clickedItems;
            }
        });
        similarHashtagRecyclerView.setLayoutManager(similarHashtagLayoutManager);
        similarHashtagRecyclerView.setAdapter(similarHashtagAdapter);

        FloatingActionButton copyButton = view.findViewById(R.id.copy_button);
        Object clipboardService = getActivity().getSystemService(CLIPBOARD_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager)clipboardService;
        copyButton.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            for (String s : clickedItem)
            {
                sb.append("#");
                sb.append(s);
                sb.append(" ");
            }
            ClipData clipData = ClipData.newPlainText("Source Text", sb.toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getActivity(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}