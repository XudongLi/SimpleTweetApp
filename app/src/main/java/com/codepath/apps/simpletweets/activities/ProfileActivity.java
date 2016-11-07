package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.fragments.ProfileHeaderFragment;
import com.codepath.apps.simpletweets.fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews(savedInstanceState);
    }

    private void initializeViews(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String source = getIntent().getStringExtra("source");
        String screenName = getIntent().getStringExtra("screenName");

        if (savedInstanceState == null) {
            ProfileHeaderFragment profileHeaderFragment = ProfileHeaderFragment.newInstance(source, screenName);
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.replace(R.id.flHeaderContainer, profileHeaderFragment, "header");
            ft.commit();
        }

    }
}
