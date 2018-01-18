package com.vegfru.twitterassignment.utility;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by subhendu on 16/01/18.
 */

public class API {
    private static final String LOG_TAG = "API";
    private static  APIClient client = null;
    static{
        client = new APIClient();
    }

    public static JSONObject getTokens() {
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("grant_type", "client_credentials");

        String query = builder.build().getEncodedQuery();
        return client.post(client.clientURLConnection(Constants.API_OAUTH_TOKEN, null), query);
    }

    public static JSONObject getTweets(String screenName){
        return client.get(client.clientURLConnection(Constants.API_FETCH_TWEETS + screenName, Utils.accessToken));
    }

    public static JSONObject searchTweets(String query){
        try {
            return client.get(client.clientURLConnection(Constants.API_SEARCH_TWEETS + URLEncoder.encode(query, "UTF-8"), Utils.accessToken));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
