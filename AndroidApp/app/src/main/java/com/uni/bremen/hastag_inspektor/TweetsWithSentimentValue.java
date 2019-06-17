package com.uni.bremen.hastag_inspektor;

public class TweetsWithSentimentValue {
    private String screenName;
    private String getUserName;
    private long id;
    private String text;
    private String randomUUID;
    private String sentimentValue;

    public TweetsWithSentimentValue(String screenName, String getUserName, long id, String text, String randomUUID) {
        this.screenName = screenName;
        this.getUserName = getUserName;
        this.id = id;
        this.text = text;
        this.randomUUID = randomUUID;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getGetUserName() {
        return getUserName;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getRandomUUID() {
        return randomUUID;
    }

    public String getSentimentValue() {
        return sentimentValue;
    }

    public void setSentimentValue(String sentimentValue) {
        this.sentimentValue = sentimentValue;
    }



    @Override
    public String toString() {
        return "TweetsWithSentimentValue{" +
                "screenName=@'" + screenName + '\'' +
                ", getUserName='" + getUserName + '\'' +
                ", id=" + id +
                ", text='" + text + '\'' +
                ", randomUUID='" + randomUUID + '\'' +
                ", sentimentValue='" + sentimentValue + '\'' +
                '}';
    }
}
