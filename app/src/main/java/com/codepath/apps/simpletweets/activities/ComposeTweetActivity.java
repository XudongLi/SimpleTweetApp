package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.applications.TwitterApplication;
import com.codepath.apps.simpletweets.clients.TwitterClient;
import com.codepath.apps.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    private TwitterClient client;
    private EditText etTweet;
    private Button btTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        client = TwitterApplication.getRestClient();
        initializeViews();
    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        etTweet = (EditText) findViewById(R.id.etTweet);
        btTweet = (Button) findViewById(R.id.btTweet);
        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = etTweet.getText().toString();
                updateStatuses(status);
            }
        });
    }

    private void updateStatuses(String status) {
        RequestParams params = new RequestParams();
        params.put("status", status);

        client.updateStatuses(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                Tweet tweetCreated = Tweet.fromJSON(response);
                Intent result = new Intent();
                result.putExtra("newTweet", Parcels.wrap(tweetCreated));
                setResult(RESULT_OK, result);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }


}
