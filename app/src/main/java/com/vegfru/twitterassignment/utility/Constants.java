package com.vegfru.twitterassignment.utility;

/**
 * Created by subhendu on 16/01/18.
 */

public class Constants {
    static final String PACKAGE_NAME = "com.vegfru.twitterassignment";

    static final String HOSTNAME = "https://api.twitter.com";
    public static final String SCREEN_NAME = "dutta_da";                                        //the app is registered in twitter with
                                                                                                // this screen name used for initial loading

    public static final String CONSUMER_KEY = "EcXpV6OolDA4psAjKwC0ODOw8";                      //consumer key after creating the app in twitter
    static final String CONSUMER_SECRET = "D4dvnXrju0ly4olrupvbDiZPPNhhr08pjr814mNFsRCihomxjx"; //consumer secret
                                                                                                // after creating the app in twitter

    static final String API_OAUTH_TOKEN = "/oauth2/token";                                      //Authentication API
    static final String API_FETCH_TWEETS = "/1.1/statuses/user_timeline.json?screen_name=";     //GET User tweets using screen name API
    static final String API_SEARCH_TWEETS = "/1.1/search/tweets.json?q=";                       //Get Search resultAPI

    static final int CONNECT_TIMEOUT = 35000;                                                   // HTTP Connection timeout
    public static final int UPDATE_INTERVAL = 5000;                                             // periodic search update interval
}
