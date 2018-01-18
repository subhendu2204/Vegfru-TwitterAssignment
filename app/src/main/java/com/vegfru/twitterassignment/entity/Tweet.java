package com.vegfru.twitterassignment.entity;

import com.vegfru.twitterassignment.utility.Json;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by subhendu on 16/01/18.
 */

public class Tweet {

    private Date dateCreated;
    private String id;
    private String text;

    private TwitterUser user;

    public Tweet(JSONObject tweetJson){
        id = Json.getString(tweetJson, "id");
        String date = Json.getString(tweetJson, "created_at");

        //date string in format E MMM DD HH:mm:ss Z yyyy ex: (Fri Sep 21 22:51:18 +0000 2012)
        SimpleDateFormat formatter = new SimpleDateFormat("E MMM DD HH:mm:ss Z yyyy");
        try {
            dateCreated = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        text = Json.getString(tweetJson, "text");
        user = new TwitterUser(Json.getJsonObject(tweetJson, "user"));

    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getText() {
        return text;
    }

    public TwitterUser getUser() {
        return user;
    }

    @Override
    public String  toString(){
        return text;
    }
}
