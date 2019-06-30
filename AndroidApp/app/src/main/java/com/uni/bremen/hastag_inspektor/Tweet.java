package com.uni.bremen.hastag_inspektor;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class Tweet {
    private String account;
    private String content;
    private String avatar;
    private Date timestamp;
    private String link;
    private ArrayList<String> hashtagList = new ArrayList<String>();

    public Tweet (Status s) {
        this.account = "@" + s.getUser().getScreenName();
        this.content = s.getText();
        this.avatar = s.getUser().get400x400ProfileImageURLHttps();
        this.timestamp = s.getCreatedAt();
        this.link = "https://twitter.com/" + s.getUser().getScreenName()
                + "/status/" + s.getId();
        Pattern MY_PATTERN = Pattern.compile("#(\\w+)");
        Matcher mat = MY_PATTERN.matcher(s.getText());
        while (mat.find()) {
            hashtagList.add(mat.group(1).toLowerCase());
        }


    }
    public String getLink ( ) {
        return link;
    }
    public void setAccount (String account) {
        this.account = account;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public void setAvatar (String avatar) {
        this.avatar = avatar;
    }

    public void setTimestamp (Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setHashtagList (ArrayList<String> hashtagList) {
        this.hashtagList = hashtagList;
    }

    public String getAccount ( ) {
        return account;
    }

    public String getContent ( ) {
        return content;
    }
    
    public String getAvatar ( ) {
        return avatar;
    }

    public Date getTimestamp ( ) {
        return timestamp;
    }

    public ArrayList<String> getHashtagList ( ) {
        return hashtagList;
    }
}



