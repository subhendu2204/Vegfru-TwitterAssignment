package com.vegfru.twitterassignment.utility;


/**
 * Created by subhendu on 16/01/18.
 */

import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static CharSequence getPostTime(Date date) {
        try {
            CharSequence simpleDate = DateUtils.getRelativeTimeSpanString(
                    date.getTime(), Calendar.getInstance().getTimeInMillis(),
                    DateUtils.MINUTE_IN_MILLIS);

            if (simpleDate.equals("0 minutes ago"))
                simpleDate = date.toString();

            return simpleDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Just Now";
    }
}
