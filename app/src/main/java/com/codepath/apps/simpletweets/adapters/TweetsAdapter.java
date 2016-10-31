package com.codepath.apps.simpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.DateParser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvScreenName;
        public TextView tvTimeStamp;
        public TextView tvBody;
        public ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvBody = (TextView) itemView.findViewById(R.id.tvTweetBody);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfile);
        }
    }

    private List<Tweet> mTweets;
    private Context mContext;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tweet tweet = mTweets.get(position);

        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvTimeStamp.setText(DateParser.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycled view
        Picasso.with(getmContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public Context getmContext() {
        return mContext;
    }
}
