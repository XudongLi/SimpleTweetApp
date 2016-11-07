package com.codepath.apps.simpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.ProfileActivity;
import com.codepath.apps.simpletweets.adapters.TweetsAdapter;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        rvTweets.setAdapter(aTweets);
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        User user = tweets.get(position).getUser();
                        Intent i = new Intent(getActivity(), ProfileActivity.class);
                        i.putExtra("source", "user");
                        i.putExtra("screenName", user.getScreenName());
                        startActivity(i);
                    }
                }
        );
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(), tweets);
    }

    public void setListenersOnStart(LinearLayoutManager linearLayoutManager,
                                    RecyclerView.OnScrollListener scrollListener,
                                    SwipeRefreshLayout.OnRefreshListener swipeRefreshListener) {
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(scrollListener);
        swipeContainer.setOnRefreshListener(swipeRefreshListener);
    }

    public void refreshTweets(List<Tweet> newTweets) {
        tweets.clear();
        aTweets.notifyDataSetChanged();
        tweets.addAll(newTweets);
        aTweets.notifyItemRangeInserted(0, newTweets.size());
    }

    public void populateTweets(List<Tweet> newTweets) {
        int curSize = aTweets.getItemCount();
        tweets.addAll(newTweets);
        aTweets.notifyItemRangeInserted(curSize, newTweets.size());
    }

    public void addNewTweet(Tweet newTweet) {
        tweets.add(0, newTweet);
        aTweets.notifyDataSetChanged();
    }

    public void setRefreshingStateFalse() {
        swipeContainer.setRefreshing(false);
    }
}
