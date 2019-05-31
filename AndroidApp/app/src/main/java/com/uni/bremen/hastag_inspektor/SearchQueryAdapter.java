package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.SearchQueryViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public SearchQueryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class SearchQueryViewHolder extends RecyclerView.ViewHolder {

        public TextView searchText;

        public SearchQueryViewHolder(@NonNull View itemView) {
            super(itemView);
            searchText = itemView.findViewById(R.id.search_query);
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
}
