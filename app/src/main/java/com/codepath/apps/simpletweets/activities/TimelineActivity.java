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
import com.codepath.apps.simpletweets.adapters.TweetsAdapter;
import com.codepath.apps.simpletweets.applications.TwitterApplication;
import com.codepath.apps.simpletweets.clients.TwitterClient;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final int TIMELINE_COUNT = 25;

    private Toolbar toolbar;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;

    // Field for TwitterClient Pagination
    private long sinceId;
    private long maxId;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initializeViews();
        client = TwitterApplication.getRestClient();
        sinceId = 1L;
        maxId = Long.MAX_VALUE - 1; // call twitter api with Long.MAX_VALUE will get internal failure
        populateTimeline();
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateTimeline() {
        RequestParams params = new RequestParams();
        params.put("count", TIMELINE_COUNT);
        params.put("max_id", maxId);

        client.getHomeTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                int curSize = aTweets.getItemCount();
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                tweets.addAll(newTweets);
                aTweets.notifyItemRangeInserted(curSize, newTweets.size());
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
                tweets.clear();
                aTweets.notifyDataSetChanged();
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                tweets.addAll(newTweets);
                aTweets.notifyItemRangeInserted(0, newTweets.size());
                maxId = newTweets.get(newTweets.size() - 1).getUid() - 1; // The id of last tweet minus 1
                swipeContainer.setRefreshing(false);
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
            tweets.add(0, newTweet);
            aTweets.notifyDataSetChanged();
        }
    }
}
