package com.codepath.apps.simpletweets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateParser {

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeTime = "";
        try {
            Date createdDate = sf.parse(rawJsonDate);
            long dateMillis = createdDate.getTime();
            long diff = (System.currentTimeMillis() - dateMillis)/1000;
            if (diff < 5)
                relativeTime = "Just now";
            else if (diff < 60)
                relativeTime = String.format(Locale.ENGLISH, "%ds",diff);
            else if (diff < 60 * 60)
                relativeTime = String.format(Locale.ENGLISH, "%dm", diff / 60);
            else if (diff < 60 * 60 * 24)
                relativeTime = String.format(Locale.ENGLISH, "%dh", diff / (60 * 60));
            else if (diff < 60 * 60 * 24 * 30)
                relativeTime = String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24));
            else {
                Calendar now = Calendar.getInstance();
                Calendar then = Calendar.getInstance();
                then.setTime(createdDate);
                if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                    relativeTime = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                            + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
                } else {
                    relativeTime = String.valueOf(then.get(Calendar.DAY_OF_MONTH)) + " "
                            + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                            + " " + String.valueOf(then.get(Calendar.YEAR) - 2000);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeTime;
    }
}
