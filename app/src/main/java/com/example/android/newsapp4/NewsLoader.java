package com.example.android.newsapp4;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();
    /** Query URL */
    private String mUrl;

    @Override
    protected void onStartLoading() {
        Log.v("EQL:onStartLoading", "forceLoad");
        forceLoad();
    }

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public ArrayList<News> loadInBackground() {
        if (mUrl == null) {
            Log.v("loadInBackground", "No URL given (or taken). Exit without trying to read news...");
            return null;
        }
        try {
            Log.v("EQLoader Background", "Try to extractNews (LOAD IN BACKGROUND)");
            // Perform the network request, parse the response, and extract a list of news articles.
            ArrayList<News> newsArticles = QueryUtils.extractNews(mUrl);
            return newsArticles;
        } catch (IOException e) {
            Log.e("NewsLoader", "Problem parsing the news article JSON results", e);
        }
        return null;
    }
}