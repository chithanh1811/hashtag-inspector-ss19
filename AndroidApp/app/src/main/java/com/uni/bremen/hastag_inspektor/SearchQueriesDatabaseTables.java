package com.uni.bremen.hastag_inspektor;

import android.provider.BaseColumns;

public class SearchQueriesDatabaseTables {

    private SearchQueriesDatabaseTables() {
    }

    public static final class SearchQueryEntry implements BaseColumns {
        public static final String TABLE_NAME = "searchList";
        public static final String COLUMN_NAME = "query";
    }

}
