package com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyserParser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("score")
    @Expose
    private Double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Example withId(String id) {
        this.id = id;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Example withScore(Double score) {
        this.score = score;
        return this;
    }

}
