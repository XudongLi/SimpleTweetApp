package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweets.applications.TwitterApplication;
import com.codepath.apps.simpletweets.clients.TwitterClient;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {
    private static final int TIMELINE_COUNT = 25;

    private TwitterClient client;

    // Field for TwitterClient Pagination
    private long sinceId;
    private long maxId;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        sinceId = 1L;
        maxId = Long.MAX_VALUE - 1; // call twitter api with Long.MAX_VALUE will get internal failure
        refreshTimeline();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
        setListenersOnStart(linearLayoutManager, scrollListener, swipeRefreshListener);
        return v;
    }

    private void populateTimeline() {
        RequestParams params = new RequestParams();
        params.put("count", TIMELINE_COUNT);
        params.put("max_id", maxId);

        client.getMentionsTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                if(!newTweets.isEmpty()) {
                    populateTweets(newTweets);
                    maxId = newTweets.get(newTweets.size() - 1).getUid() - 1; // The id of last tweet minus 1
                    long newestTweetId = newTweets.get(0).getUid();
                    sinceId = (sinceId > newestTweetId) ? sinceId : newestTweetId;
                }
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

        client.getMentionsTimeline(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());
                List<Tweet> newTweets = Tweet.fromJSONArray(response);
                if(!newTweets.isEmpty()) {
                    refreshTweets(newTweets);
                    maxId = newTweets.get(newTweets.size() - 1).getUid() - 1; // The id of last tweet minus 1
                    setRefreshingStateFalse();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
