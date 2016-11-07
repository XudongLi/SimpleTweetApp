package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.applications.TwitterApplication;
import com.codepath.apps.simpletweets.clients.TwitterClient;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileHeaderFragment extends Fragment {

    private TwitterClient client;
    private User user;

    private TextView tvName;
    private TextView tvTagline;
    private TextView tvFollowers;
    private TextView tvFollowings;
    private ImageView ivProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        if("self".equals(getArguments().getString("source"))) {
            populateSelfProfileHeader();
        } else if ("user".equals(getArguments().getString("source"))) {
            populateUserProfileHeader(getArguments().getString("screenName"));
        } else {
            throw new IllegalArgumentException("Unexpect source trigger this page");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);

        tvName = (TextView) v.findViewById(R.id.tvName);
        tvTagline = (TextView) v.findViewById(R.id.tvTagline);
        tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
        tvFollowings = (TextView) v.findViewById(R.id.tvFollowings);
        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);

        return v;
    }

    public static ProfileHeaderFragment newInstance(String source, String screenName) {
        ProfileHeaderFragment profileHeaderFragment = new ProfileHeaderFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        args.putString("screenName", screenName);
        profileHeaderFragment.setArguments(args);
        return profileHeaderFragment;
    }

    public void populateUserProfileHeader(String screenName) {
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);

        client.getUserInfo(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                populateHeaderView(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void populateSelfProfileHeader() {
        client.getSelfInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                populateHeaderView(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    protected void populateHeaderView(User newUser) {
        tvName.setText(newUser.getName());
        tvTagline.setText(newUser.getTagline());
        tvFollowers.setText(newUser.getFollowersCount() + " Followers");
        tvFollowings.setText(newUser.getFollowingCount() + " Followings");
        Picasso.with(getContext()).load(newUser.getProfileImageUrl()).into(ivProfile);
    }
}
