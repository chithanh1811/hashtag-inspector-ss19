package com.uni.bremen.hastag_inspektor.MicrosoftSentimentAnalyseTool;

import java.util.ArrayList;
import java.util.List;

public class Document {
    public String id, language, text;

    public Document(String id, String language, String text){
        this.id = id;
        this.language = language;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id='" + id + '\'' +
                ", language='" + language + '\'' +
                ", text='" + text + '\'' +
                '}';
    }


}
