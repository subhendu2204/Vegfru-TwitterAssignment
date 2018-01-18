package com.vegfru.twitterassignment;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vegfru.twitterassignment.adapters.TweetListAdapter;
import com.vegfru.twitterassignment.entity.Tweet;
import com.vegfru.twitterassignment.utility.API;
import com.vegfru.twitterassignment.utility.Constants;
import com.vegfru.twitterassignment.utility.Json;
import com.vegfru.twitterassignment.utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "MAIN";
    private LinearLayout loginLayout;
    private Button loginBtn;
    private LinearLayout searchBar;
    private EditText searchEditBox;
    private LinearLayout searchBtn;
    Toolbar toolbar;
    AppBarLayout appBarLayout;

    private RecyclerView tweetRecyclerView;                 //recycler view that list fetched tweets
    private TweetListAdapter tweetListAdapter;              //view adapter for the recycler view
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView.LayoutManager layoutManager;

    private android.os.Handler mHandler;                    //handler used to invoke timer like behaviour
    private boolean isSearching = false;                    // when searching is ongoing, this flag set to true, for app state management
    private boolean isBackGround = false;                   // to check whether app is in foreground or background
    private boolean isSwiped = false;                       // special flag to differentiate page refresh behaviour
                                                            // based on swipe by user or periodic update

    AsyncTask<String, Void, JSONObject> downloadsTweetTask = null;      //Async Task for authentication app to twitter and
                                                                        // also load initial user data
    AsyncTask<String, Void, JSONObject> searchTweetsTask = null;        //Async Task for process search hashtag query twitter api
                                                                        // also initiate timer using handler
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.loadAccessToken(this);
        initViews();
        initTweetList();
        if (savedInstanceState != null && savedInstanceState.getBoolean("PAGE_REFRESHING")) {
            searchString = savedInstanceState.getString("SEARCH_STRING");
            searchTweets();
        }else {
            loadTweets();
        }
    }

    /*-------------------- Start Important App state processing --------------------------*/

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(isSearching) {
            outState.putBoolean("PAGE_REFRESHING", true);
            outState.putString("SEARCH_STRING", searchString);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause(){
        cleanUp();
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        isBackGround = false;
        if(isSearching) {
            searchTweets();
        }
        else{
            if(downloadsTweetTask == null || downloadsTweetTask.isCancelled()){
                loadTweets();
            }
        }
    }

    @Override
    protected void onDestroy(){
        cleanUp();
        isSearching = false;
        super.onDestroy();
    }

    /*-------------------- End Important App state processing --------------------------*/

    /*-------------------- Start Initialize views and layout --------------------------*/
    //initialize views
    private void initViews(){
        mHandler = new android.os.Handler();
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initLoginViews();
    }

    //initialize tweet list view
    private void initTweetList(){
        tweetRecyclerView = (RecyclerView) findViewById(R.id.tweetsRecycleView);
        layoutManager = new LinearLayoutManager(this);
        tweetRecyclerView.setLayoutManager(layoutManager);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwiped = true;
                swipeRefresh.setRefreshing(true);
                if(isSearching){
                    searchTweets();
                }else{
                    loadTweets();
                }
            }
        });
        searchBar = (LinearLayout) findViewById(R.id.searchBar);
        searchEditBox = (EditText) findViewById(R.id.searchEditBox);
        searchBtn = (LinearLayout) findViewById(R.id.searchbutton);
        searchBtn.setOnClickListener(this);
    }

    //show login views
    private void initLoginViews(){
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        if(Utils.accessToken == null){
            loginLayout.setVisibility(View.VISIBLE);
        }
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
    }
    /*-------------------- End Initialize views and layout --------------------------*/

    /*-------------------- Start Implementing Click Listeners --------------------------*/

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.loginBtn:
                loginLayout.setVisibility(View.GONE);
                loadTweets();
                break;
            case R.id.searchbutton:
                searchString = searchEditBox.getText().toString();
                hideIme();
                searchTweets();
                break;
            default:

        }
    }

    /*-------------------- End Implementing Click Listeners --------------------------*/

    /*-------------------- Start Authentication and initial tweet processing --------------------------*/

    //this function is basically wrapper for the async task for authentication and intial data fetching
    private void loadTweets(){
        if(loginLayout.getVisibility() != View.VISIBLE) {
            if (downloadsTweetTask != null) {
                downloadsTweetTask.cancel(true);
            }
            downloadsTweetTask = new DownloadTweetsTask();
            downloadsTweetTask.execute(Constants.SCREEN_NAME);
        }
    }


    //authenticate app with twitter and download tweets
    //here one async task is created to update main layout
    private class DownloadTweetsTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            swipeRefresh.setVisibility(View.VISIBLE);
            swipeRefresh.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject result = null;
            if(!isBackGround){
                if(strings.length > 0){
                    if(Utils.accessToken == null){
                        //call authentication api
                        JSONObject auth_response = API.getTokens();
                        JSONObject error = Utils.checkError(auth_response);
                        if(error != null){
                            return error;
                        }else{
                            //not saved shared preference, it will cause UI thread blocking
                            Utils.accessToken = Json.getString(Utils.getJsonObjFromResp(auth_response), "access_token");
                            Utils.token_type = Json.getString(Utils.getJsonObjFromResp(auth_response), "token_type");
                        }
                    }
                    //call get tweeters download api
                    //to prevent empty list, user created tweets are used
                    if(Utils.accessToken != null) {
                        result = API.getTweets(strings[0]);
                        JSONObject error = Utils.checkError(result);
                        if(error != null){
                            return error;
                        }
                    }

                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject resultJson) {
            if(resultJson != null){
                if(Json.getString(resultJson, "error") != null){

                    String error = Json.getString(resultJson, "error");
                    display(error);
                }else{

                    JSONArray result = Utils.getJsonArrayFromResp(resultJson);
                    int i, len;
                    if (result != null) {
                        ArrayList<Tweet> tweets = new ArrayList<>();
                        len = result.length();
                        for (i = 0; i < len; ++i){
                            JSONObject tweetJson = Json.getJsonObject(result, i);
                            Tweet tweet = new Tweet(tweetJson);
                            tweets.add(tweet);
                        }
                        if(tweetListAdapter == null){
                            tweetListAdapter = new TweetListAdapter(MainActivity.this);
                            tweetListAdapter.assign(tweets);
                            tweetRecyclerView.setAdapter(tweetListAdapter);
                        }else{
                            tweetListAdapter.assign(tweets);
                        }
                        tweetListAdapter = null;

                    }
                }
                isSwiped = false;
                swipeRefresh.setRefreshing(false);
            }
            //if error happens in login page
            if(Utils.accessToken == null){
                swipeRefresh.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }else{
                //set toolbar here
                if(appBarLayout.getVisibility() != View.VISIBLE){
                    appBarLayout.setVisibility(View.VISIBLE);
                    setSupportActionBar(toolbar);
                }
                searchBar.setVisibility(View.VISIBLE);
            }
        }
    }

    /*-------------------- End Authentication and initial tweet processing --------------------------*/


    /*-------------------- Start Search tweet processing --------------------------*/

    //this function is basically wrapper for the async task for search query processing
    private void searchTweets(){

        if(searchString != null && !searchString.trim().isEmpty()){
            if(!searchString.contains("#")){
                searchString = "#" + searchString;
            }
            if(searchTweetsTask != null){
                searchTweetsTask.cancel(true);
            }
            searchTweetsTask = new SearchTweetsTask();
            searchTweetsTask.execute(searchString);
        }else{
            display(getResources().getString(R.string.search_text_empty_err));
        }

    }

    //In this task in background search api of twitter is called
    // after publising the result in recycler view it start handler to create loop
    private class SearchTweetsTask extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            if(swipeRefresh.getVisibility() != View.VISIBLE) {
                swipeRefresh.setVisibility(View.VISIBLE);
            }
            swipeRefresh.setRefreshing(true);
            isSearching = true;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject result = null;
            if(!isBackGround){
                if(strings.length > 0){
                    if(Utils.accessToken == null){
                        //you have to logged in first
                        result = new JSONObject();
                        Json.put(result, "error", "You have to logged in first");
                    }else{
                        //call get tweeters download api
                        result = API.searchTweets(strings[0]);
                        JSONObject error = Utils.checkError(result);
                        if(error != null){
                            return error;
                        }
                    }

                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject resultJson) {
            if(resultJson != null){
                if(Json.getString(resultJson, "error") != null){

                    String error = Json.getString(resultJson, "error");
                    display(error);
                }else{

                    JSONObject obj = Utils.getJsonObjFromResp(resultJson);
                    JSONArray result = Json.getJsonArray(obj, "statuses");
                    int i, len;
                    if (result != null) {
                        ArrayList<Tweet> tweets = new ArrayList<>();
                        len = result.length();
                        for (i = 0; i < len; ++i){
                            JSONObject tweetJson = Json.getJsonObject(result, i);
                            Tweet tweet = new Tweet(tweetJson);
                            tweets.add(tweet);
                        }
                        if(tweetListAdapter == null){
                            tweetListAdapter = new TweetListAdapter(MainActivity.this);
                            tweetListAdapter.assign(tweets);
                            tweetRecyclerView.setAdapter(tweetListAdapter);
                        }else{
                            tweetListAdapter.assign(tweets);
                        }

                    }
                }
                swipeRefresh.setRefreshing(false);
                if(!isSwiped) {
                    //here message is sent to handler which in turn call this task again after specified time
                    mHandler.postDelayed(r, Constants.UPDATE_INTERVAL);
                }else{
                    //this is when user swiped down to refresh, so no periodic task should not be created
                    isSwiped = false;
                }
            }
        }
    }

    //this thread/message again initiate the search async task
    Runnable r = new Runnable() {
        @Override
        public void run() {
            searchTweets();
        }
    };

    /*-------------------- End Search tweet processing --------------------------*/

    /*-------------------- Utility Functions --------------------------*/

    //clean up necessary references when state changes to Pause or Destroy
    private void cleanUp(){
        isBackGround = true;
        if(downloadsTweetTask != null){
            downloadsTweetTask.cancel(true);
            downloadsTweetTask = null;
        }
        if(searchTweetsTask != null){
            searchTweetsTask.cancel(true);
            searchTweetsTask = null;
        }
        Utils.setAccessToken(Utils.accessToken, Utils.token_type, this);
    }

    //show toast msg
    private void display(String error){
        Toast toast = Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT);
        toast.show();
    }

    //hide ime
    private void hideIme(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
