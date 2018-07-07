package com.example.android.newsapp4;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * {@link News} represents a news article with its data.
 * It contains the news title, section name, author if available, time if available and weblink url
 */
public class News {

    /**
     * News title (webTitle)
     **/
    private String mTitle;

    /**
     * News section name (sectionName)
     **/
    private String mSectionName;

    /**
     * News time (webPublicationDate) in format: 2018-05-27T08:00:20Z
     **/
    private String mTime;

    /**
     * Article URL (webUrl)
     **/
    private String mUrl;

    /**
     * Constructor that has exactly the same name as its class
     *
     * @param newsTitle       The news title, may contain an author name after a |
     * @param newsSectionName Section name or names
     * @param newsTimestamp   Time the article was posted in format: 2018-05-27T08:00:20Z
     * @param newsUrl         News Url
     */
    public News(String newsTitle, String newsSectionName, String newsTimestamp, String newsUrl) {
        mTitle = newsTitle;
        mSectionName = newsSectionName;
        mTime = newsTimestamp;
        mUrl = newsUrl;
    }

    /**
     * Constructor that has exactly the same name as its class, NO TIMESTAMP
     *
     * @param newsTitle       The news title, may contain an author name after a |
     * @param newsSectionName Section name or names
     * @param newsUrl         News Url
     */
    public News(String newsTitle, String newsSectionName, String newsUrl) {
        mTitle = newsTitle;
        mSectionName = newsSectionName;
        mUrl = newsUrl;
    }

    /**
     * Method: Get the title of the news article
     * May or may not contain an author after a |
     *
     * @return Title with or without author
     */
    public String getNewsTitle() {
        return mTitle;
    }

    /**
     * Method: get timestamp of article
     *
     * @return String of style: 2018-05-27T08:00:20Z
     */
    public String getTimestamp() {
        return mTime;
    }

    /**
     * Method: get timestamp of article
     *
     * @return String of style: 2018-05-27T08:00:20Z
     */
    public String getSimpleTimestamp() {

        // Get the time String with specific pattern
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // Set the timezone of the article
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Set output format for time string
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        // Change timezone to be displayed at user's phone timezone
        output.setTimeZone(TimeZone.getDefault());
        try {
            Date d = sdf.parse(mTime);
            String formattedTime = output.format(d);
            return formattedTime;
        } catch (ParseException e) {
            Log.v("News", "date parse excetion error");
        }
        return mTime;
    }

    /**
     * Method: get section name or names
     *
     * @return String with section names
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Method: get URL
     *
     * @return String URL
     */
    public String getUrl() {
        return mUrl;
    }
}
