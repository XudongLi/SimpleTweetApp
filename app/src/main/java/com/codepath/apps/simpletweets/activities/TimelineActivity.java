package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.applications.TwitterApplication;
import com.codepath.apps.simpletweets.clients.TwitterClient;
import com.codepath.apps.simpletweets.fragments.TweetsListFragment;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final int TIMELINE_COUNT = 25;

    private TweetsListFragment tweetsListFragment;
    private Toolbar toolbar;
    private TwitterClient client;

    // Field for TwitterClient Pagination
    private long sinceId;
    private long maxId;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initializeViews();
        retrieveFragment(savedInstanceState);
        client = TwitterApplication.getRestClient();
        sinceId = 1L;
        maxId = Long.MAX_VALUE - 1; // call twitter api with Long.MAX_VALUE will get internal failure
        populateTimeline();
    }

    private void retrieveFragment(Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }
        };
        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        };
        if (savedInstanceState == null) {
            tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
        tweetsListFragment.setListenersOnStart(linearLayoutManager, scrollListener, swipeRefreshListener);
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void populateTimeline() {
        RequestParams params = new RequestParams();
        params.put("count", TIMELINE_COUNT);
        params.put("max_id", maxId);

        client.getHomeTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                tweetsListFragment.populateTweets(newTweets);
                maxId = newTweets.get(newTweets.size() - 1).getUid() - 1; // The id of last tweet minus 1
                long newestTweetId = newTweets.get(0).getUid();
                sinceId = (sinceId > newestTweetId) ? sinceId : newestTweetId;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void refreshTimeline() {
        RequestParams params = new RequestParams();
        params.put("count", TIMELINE_COUNT);

        client.getHomeTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                tweetsListFragment.refreshTweets(newTweets);
                maxId = newTweets.get(newTweets.size() - 1).getUid() - 1; // The id of last tweet minus 1
                tweetsListFragment.setRefreshingStateFalse();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_composite) {
            Intent i = new Intent(getApplicationContext(), ComposeTweetActivity.class);
            startActivityForResult(i, 20);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 20) {
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("newTweet"));
            tweetsListFragment.addNewTweet(newTweet);
        }
    }
}
