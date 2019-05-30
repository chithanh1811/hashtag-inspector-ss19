package com.uni.bremen.hastag_inspektor;

public class HashtagAndOccurences {
    private String hashtagName;
    private int numberOfOccurrences;

    public HashtagAndOccurences(String hashtagName, int numberOfOccurrences) {
        this.hashtagName = hashtagName;
        this.numberOfOccurrences = numberOfOccurrences;
    }

    public String getHashtagName() {
        return hashtagName;
    }

    public void setHashtagName(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public int getNumberOfOccurrences() {
        return numberOfOccurrences;
    }

    public void setNumberOfOccurrences(int numberOfOccurrences) {
        this.numberOfOccurrences = numberOfOccurrences;
    }

    @Override
    public String toString() {
        return "HashtagAndOccurences{" +
                "hashtagName='" + hashtagName + '\'' +
                ", numberOfOccurrences=" + numberOfOccurrences +
                '}';
    }
}
