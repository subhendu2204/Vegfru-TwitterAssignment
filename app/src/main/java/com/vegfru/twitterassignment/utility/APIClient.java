package com.vegfru.twitterassignment.utility;

/**
 * Created by subhendu on 16/01/18.
 */

import android.content.Context;
import android.util.Base64;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

class APIClient {
    private static final String LOG_TAG = "APIClient";
    //configuring client url connection
    //params :: urlString: url to connect with, message: json string of parameters, accessToken: access token
    //response:: HTTPURLConnection instance
    private String currentUrl = null;
    HttpURLConnection clientURLConnection(String urlString, String accessToken){
        HttpURLConnection connection = null;
        currentUrl = urlString;
        try{
            urlString = Constants.HOSTNAME + urlString;
            Log.d(LOG_TAG, "clientURLConnection - url: " + urlString);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            //TIMEOUT
            connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
            connection.setReadTimeout(Constants.CONNECT_TIMEOUT);
            //set request property if access token present
            if(accessToken != null){
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }else{
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(Constants.CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(Constants.CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                connection.setRequestProperty("Authorization", "Basic " + base64Encoded);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
    //handle all http methods GET, POST, PUT, DEL
    //param:: connection: an HttpURLConnection object, method: a string defining http methods
    //return: JSON object
    private JSONObject handleHttpConnection(HttpURLConnection connection, String method, String message){
        JSONObject response = null;
        try{
            if (connection != null) {
                connection.setRequestMethod(method);
                if(message != null){
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(message);
                    writer.flush();
                    writer.close();
                    os.close();
                }
                connection.connect();
                Log.d(LOG_TAG, "response code: " + connection.getResponseCode() + " msg: " + connection.getResponseMessage());
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    String content = Utils.readTextResponse(is);
                    if(content == null || content.equalsIgnoreCase("OK")){
                        content = "{'status':'OK'}";
                    }
                    response = new JSONObject();
                    Json.put(response, "response", content);
                    is.close();
                }else if (connection.getResponseCode() == 401) {
                    if(Utils.accessToken == null){
                        //do nothing get Token API called,user must be logged out
                        //Utils.logout(mContext);
                    }else{
                        //log out should be called, because refresh token API can not be unauthorized
                        response = new JSONObject();
                        JSONObject status = new JSONObject();
                        Json.put(status, "status", "unauthorized");
                        Json.put(response, "response", status);
                    }
                }else if(connection.getResponseCode() == 400 || connection.getResponseCode() == 404){
                    InputStream is = connection.getErrorStream();
                    response = new JSONObject();
                    String error = Utils.readTextResponse(is);
                    String respMsg = connection.getResponseMessage();
                    JSONObject errorJson;
                    try{
                        errorJson = new JSONObject(error);
                        if(Json.getString(errorJson, "error") == null){
                            error = connection.getResponseMessage();
                            errorJson.put("error", error);
                        }
                    }catch (JSONException ex){
                        errorJson = new JSONObject();
                        if(error != null){
                            errorJson.put("error", error);
                        }else{
                            errorJson.put("error", respMsg);
                        }
                    }
                    errorJson.put("code", connection.getResponseCode());
                    response.put("error", errorJson);
                }

            }
        }catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally{
            if(connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
    // post http request handler
    JSONObject post(HttpURLConnection connection, String message){
        return handleHttpConnection(connection, "POST", message);
    }
    // get http request handler
    public JSONObject get(HttpURLConnection connection){
        return handleHttpConnection(connection, "GET", null);
    }
    // put http request handler
    public JSONObject put(HttpURLConnection connection, String message){
        return handleHttpConnection(connection, "PUT", message);
    }
    // delete http request handler
    JSONObject delete(HttpURLConnection connection){
        return handleHttpConnection(connection, "DELETE", null);
    }
}
