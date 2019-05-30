package com.uni.bremen.hastag_inspektor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchQueryDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "searchQuery.db";
    public static final int DATABASE_VERSION = 1;


    public SearchQueryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SEARCH_QUERY_TABLE = "CREATE TABLE " +
                SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME + " (" +
                SearchQueriesDatabaseTables.SearchQueryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SearchQueriesDatabaseTables.SearchQueryEntry.COLUMN_NAME + " TEXT NOT NULL " +
                ");";
        db.execSQL(SQL_CREATE_SEARCH_QUERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchQueriesDatabaseTables.SearchQueryEntry.TABLE_NAME);
        onCreate(db);
    }
}
