package com.uni.bremen.hastag_inspektor;

public class SearchDB {
    public int id;
    public String Query;

    public SearchDB(int id, String query) {
        this.id = id;
        Query = query;
    }

    public int getId() {
        return id;
    }

    public String getQuery() {
        return Query;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuery(String query) {
        Query = query;
    }
}
