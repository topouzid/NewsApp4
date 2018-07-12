package com.example.android.newsapp4;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /** URL for news data from the Guardian dataset */
    private static final String ONLINE_JSON_URL = "https://content.guardianapis.com/search";
    private static final String FULL_ONLINE_JSON_URL = "https://content.guardianapis.com/search?q=technology%20AND%20android&show-tags=contributor&page-size=20&from-date=2018-01-01&api-key=10ae015c-6c0c-4023-b776-71b81c829b45";
    private static final String GUARDIAN_KEY = "10ae015c-6c0c-4023-b776-71b81c829b45";
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String topicChoice = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(ONLINE_JSON_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `page-size=20`
        uriBuilder.appendQueryParameter("q", "technology AND " + topicChoice);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("from-date", "2018-01-01");
        uriBuilder.appendQueryParameter("api-key", GUARDIAN_KEY);
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyTextView.setText(getResources().getText(R.string.communicating));//"Communicating with the journalists. Please wait..."
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        Log.v("onLoaderReset", "Loader reset: Cleaning UI...");
        cleanUi();
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> newsArticles) {
        if (newsArticles == null) {
            mEmptyTextView.setText(getResources().getText(R.string.communicating));//"Communicating with the journalists. Please wait..."
            return;
        }
        mProgressBar.setVisibility(View.GONE);
        cleanUi();
        mEmptyTextView.setText(getResources().getText(R.string.no_news_found));//"No recent technology news found");
        updateUi(newsArticles);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText(getResources().getText(R.string.no_network));//"No network connection");
        }
    }

    /**
     * Test onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("MainActivity", "onResume");
    }

    private void updateUi(final ArrayList<News> newsArticles) {
        // Create a new custom {@link NewsAdapter} of news articles
        NewsAdapter adapter = new NewsAdapter(this, newsArticles);
        int eqCount = newsArticles.size();
        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(adapter);
        newsListView.setEmptyView(mEmptyTextView);
        newsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /** get the {@link Earthquake} object at the current position where the user clicked on */
                        News currentArticle = newsArticles.get(position);
                        /** get the url from the current article */
                        String url = currentArticle.getUrl();
                        /** create a browser intent */
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        /** set the URL and send it to the browser */
                        browserIntent.setData(Uri.parse(url));
                        /** start browser */
                        startActivity(browserIntent);
                    }
                }
        );

    }

    /**
     * Method to clean the UI (and adapter)
     */
    private void cleanUi() {
        ListView newsListView = (ListView) findViewById(R.id.list);
        newsListView.setAdapter(null);
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
