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



    public int getNumberOfOccurrences() {
        return numberOfOccurrences;
    }

    @Override
    public String toString() {
        return "HashtagAndOccurences{" +
                "hashtagName='" + hashtagName + '\'' +
                ", numberOfOccurrences=" + numberOfOccurrences +
                '}';
    }
}
