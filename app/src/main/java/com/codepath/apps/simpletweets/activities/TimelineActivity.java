package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.TweetsListFragment;
import com.codepath.apps.simpletweets.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {

    private static final int TIMELINE_COUNT = 25;

    private TweetsListFragment tweetsListFragment;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initializeViews();
        retrieveFragment(savedInstanceState);
    }

    private void retrieveFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            tweetsListFragment = (TweetsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);
        }
    }

    private void initializeViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
