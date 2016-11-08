package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
        toolbar.setNavigationIcon(android.support.design.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                setResult(RESULT_CANCELED, result);
                finish();
            }
        });

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
