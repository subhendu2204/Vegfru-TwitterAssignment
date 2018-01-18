package com.vegfru.twitterassignment.utility;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by subhendu on 16/01/18.
 */

public class Utils {
    public static String accessToken;
    public static String token_type;


    private static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public static void setAccessToken(String token, String type, Context context){
        accessToken = token;
        token_type = type;
        saveToPref("access_token", accessToken, context);
        saveToPref("token_type", token_type, context);
    }

    public static void loadAccessToken(Context context){
        accessToken = getSharedPref(context).getString("access_token", null);
        token_type = getSharedPref(context).getString("token_type", null);
        //init image loader here
        initImageLoader(context);
    }

    public  static void initImageLoader(Context context){
        // image cache initiation
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .imageDecoder(new NutraBaseImageDecoder(true))
                .defaultDisplayImageOptions(options)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(config);
        }
    }

    //store value to shared pref
    public static void saveToPref(String key, String value, Context context){
        SharedPreferences pref = getSharedPref(context);
        if(pref != null){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    //Helper class to read text response
    private static class ByteBuffer {
        byte[] buffer = new byte[256];
        int write;
        public void put(byte[] buf, int len) {
            ensure(len);
            System.arraycopy(buf, 0, buffer, write, len);
            write += len;
        }
        private void ensure(int amt) {
            int req = write + amt;
            if (buffer.length <= req) {
                byte[] temp = new byte[req * 2];
                System.arraycopy(buffer, 0, temp, 0, write);
                buffer = temp;
            }
        }
    }

    // read content of given stream in form of string
    public static String readTextResponse(InputStream stream) {
        String content = "";
        try {
            BufferedInputStream in = new BufferedInputStream(stream);
            ByteBuffer buffer = new ByteBuffer();
            byte[] buf = new byte[4*1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                buffer.put(buf, len);
            }
            content = new String(buffer.buffer, 0, buffer.write);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    //get json object from response object of API call
    public static JSONObject getJsonObjFromResp(JSONObject resp){
        JSONObject json = null;
        try {
            if(resp != null && Json.getString(resp, "response") != null) {
                json = new JSONObject(Json.getString(resp, "response"));
            }
        } catch (JSONException e ) {
            e.printStackTrace();
        }
        return json;
    }
    //get json array from response object of API call
    public static JSONArray getJsonArrayFromResp(JSONObject resp){
        JSONArray json = null;
        try {
            if(resp != null && Json.getString(resp, "response") != null) {
                Object temp = new JSONTokener(Json.getString(resp, "response")).nextValue();
                if( temp instanceof JSONArray) {
                    json = (JSONArray)temp;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //check for error messages and handle it
    public static JSONObject checkError(JSONObject response){
        JSONObject errorJson = null;
        //no response from server
        if(response == null){
            errorJson = new JSONObject();
            Json.put(errorJson, "error", "Can not connect to server. Please try again later!!!");
        } else {
            //check for authorization error
            errorJson = Json.getJsonObject(response, "error");
        }
        return errorJson;
    }

}
