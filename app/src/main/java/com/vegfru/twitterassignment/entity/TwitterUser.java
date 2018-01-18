package com.vegfru.twitterassignment.entity;

import com.vegfru.twitterassignment.utility.Json;

import org.json.JSONObject;

/**
 * Created by subhendu on 16/01/18.
 */

public class TwitterUser {

    private String screenName;
    private String name;
    private String profileImageUrl;

    public TwitterUser(JSONObject userJson){
        screenName = Json.getString(userJson, "screen_name");
        name = Json.getString(userJson, "name");
        profileImageUrl = Json.getString(userJson, "profile_image_url");
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getName() {
        return name;
    }

    @Override
    public String  toString(){
        return screenName;
    }
}
