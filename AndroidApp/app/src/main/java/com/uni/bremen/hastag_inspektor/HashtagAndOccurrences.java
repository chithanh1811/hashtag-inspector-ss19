package com.uni.bremen.hastag_inspektor;

public class HashtagAndOccurrences {
    private String hashtagName;
    private int numberOfOccurrences;

    public HashtagAndOccurrences(String hashtagName, int numberOfOccurrences) {
        this.hashtagName = hashtagName;
        this.numberOfOccurrences = numberOfOccurrences;
    }

    public String getHashtagName() {
        return hashtagName;
    }



    public int getNumberOfOccurrences() {
        return numberOfOccurrences;
    }

    @Override
    public String toString() {
        return "HashtagAndOccurrences{" +
                "hashtagName='" + hashtagName + '\'' +
                ", numberOfOccurrences=" + numberOfOccurrences +
                '}';
    }
}
