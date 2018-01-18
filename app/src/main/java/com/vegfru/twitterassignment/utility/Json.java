package com.vegfru.twitterassignment.utility;

/**
 * Created by subhendu on 16/01/18.
 */

import org.json.JSONArray;
import org.json.JSONObject;

public class Json {
    public static Object get(JSONObject json, String name)
    {
        try
        {
            return json.get(name);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public static JSONObject getJsonObject(JSONObject json, String name)
    {
        try
        {
            return json.getJSONObject(name);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static String getString(JSONObject json, String name)
    {
        try
        {
            return json.getString(name);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static boolean getBoolean(JSONObject json, String name)
    {
        try
        {
            return json.getBoolean(name);
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static Integer getInteger(JSONObject json, String name)
    {
        try
        {
            return json.getInt(name);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public static int getInt(JSONObject json, String name)
    {
        try
        {
            return json.getInt(name);
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public static long getLong(JSONObject json, String name)
    {
        try
        {
            return json.getLong(name);
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public static JSONArray getJsonArray(JSONObject json, String name)
    {
        try
        {
            return json.getJSONArray(name);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static boolean put(JSONObject json, String name, Object value)
    {
        try
        {
            json.put(name, value);

            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static JSONObject getJsonObject(JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getJSONObject(index);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static String getString(JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getString(index);
        }
        catch(Exception e)
        {
            return null;
        }
    }
}
