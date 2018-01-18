package com.vegfru.twitterassignment.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vegfru.twitterassignment.R;
import com.vegfru.twitterassignment.widgets.CircularImageView;

/**
 * Created by subhendu on 16/01/18.
 */

public class TweetViewHolder extends RecyclerView.ViewHolder {

    public CircularImageView circularImageView;
    public TextView tweetText, time, name;

    public TweetViewHolder(View itemView) {
        super(itemView);
        circularImageView = (CircularImageView) itemView.findViewById(R.id.circularimage);
        tweetText = (TextView) itemView.findViewById(R.id.tweetText);
        name = (TextView) itemView.findViewById(R.id.name);
        time = (TextView) itemView.findViewById(R.id.time);

    }
}
