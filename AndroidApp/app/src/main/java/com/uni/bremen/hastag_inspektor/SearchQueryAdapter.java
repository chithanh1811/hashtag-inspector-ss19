package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.SearchQueryViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private Context context;
    private ArrayList<String> listOfAllHashtags = new ArrayList<>();

    public SearchQueryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        this.context = context;
    }

    public class SearchQueryViewHolder extends RecyclerView.ViewHolder {

        public TextView searchText;

        public SearchQueryViewHolder(@NonNull View itemView) {
            super(itemView);
            searchText = itemView.findViewById(R.id.search_query);
            searchText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = mCursor.getString(mCursor.getColumnIndex(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME));
                    // TODO @Sajjad Ich brauche noch eine Method, ein neue Anfrage (nicht von der MainActivity) zu stellen
                    Query q = new Query(query + " -filter:retweets");
                    q.setCount(50);
                    try {
                        QueryResult result = twitter.search(q);
                        for (Status status : result.getTweets()) {
                            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                            extractHashtagsFromAString(status.getText());
                            countNumberOfTweets++;
                        }
                    } catch (TwitterException te) {
                        Toast.makeText(getBaseContext(), te.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    Intent myIntent = new Intent(context, SearchResultsActivity.class);
                    myIntent.putExtra("title", query);
                    context.startActivity(myIntent);
                }
            });
        }

    }

    @NonNull
    @Override
    public SearchQueryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.items, viewGroup, false);
        return new SearchQueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchQueryViewHolder searchQueryViewHolder, int i) {
        if (!mCursor.moveToPosition(i)) {
            return;
        }
        String name = mCursor.getString(mCursor.getColumnIndex(SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME));
        searchQueryViewHolder.searchText.setText(name);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();

        }
    }

    public void extractHashtagsFromAString(String someSampleText) {
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(someSampleText);
        List<String> listOfHashtagsInTheString = new ArrayList<>();
        while (mat.find()) {
            listOfHashtagsInTheString.add(mat.group(1).toLowerCase());

        }
        for (String s : listOfHashtagsInTheString) {
            System.out.println("Hashtag: " + s);
        }
        listOfAllHashtags.addAll(listOfHashtagsInTheString);
    }
}
