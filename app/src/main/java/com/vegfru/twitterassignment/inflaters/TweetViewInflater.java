package com.vegfru.twitterassignment.inflaters;

import android.content.Context;

import com.vegfru.twitterassignment.entity.Tweet;
import com.vegfru.twitterassignment.utility.ImageRequestManager;
import com.vegfru.twitterassignment.utility.TimeUtils;
import com.vegfru.twitterassignment.viewHolders.TweetViewHolder;

import java.util.Date;

/**
 * Created by subhendu on 16/01/18.
 */

public class TweetViewInflater {

    public static void getTweets(TweetViewHolder vh, Tweet tweet, Context context){
        if(vh == null){
            return;
        }
        vh.tweetText.setText(tweet.getText());
        vh.name.setText(tweet.getUser().getScreenName());
        if (tweet.getUser().getProfileImageUrl() != null) {
            ImageRequestManager.get(context).requestImage(context, vh.circularImageView, tweet.getUser().getProfileImageUrl(), -1);
        }
        vh.time.setText(TimeUtils.getPostTime(tweet.getDateCreated()));

    }
}
