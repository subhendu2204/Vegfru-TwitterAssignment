package com.vegfru.twitterassignment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vegfru.twitterassignment.R;
import com.vegfru.twitterassignment.entity.Tweet;
import com.vegfru.twitterassignment.inflaters.TweetViewInflater;
import com.vegfru.twitterassignment.viewHolders.TweetViewHolder;

import java.util.ArrayList;

/**
 * Created by subhendu on 16/01/18.
 */

public class TweetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private Context mContext;

    public TweetListAdapter(Context context){
        mContext = context;
    }

    public void assign(ArrayList<Tweet> tweets){
        this.tweets = tweets;
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Tweet> tweets){
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        TweetViewInflater.getTweets((TweetViewHolder) holder, tweet, mContext);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }
}
