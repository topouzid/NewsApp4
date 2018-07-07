package com.example.android.newsapp4;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * Helper methods related to requesting and receiving news data from TheGuardian.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractNews(String url) throws IOException {

        URL newsUrl = createUrl(url);

        String jsonResponse = makeHttpRequest(newsUrl);
        Log.v("QueryUtils ex", "jsonResponse: \"" + jsonResponse + "\"");

        // Create an empty ArrayList that we can start adding news articles to
        /** 1st method */
        /** Create a JsonReader from the Json String */
        JsonReader reader = new JsonReader(new StringReader(jsonResponse));

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and

            /** 1st method */
            /** send the Json Reader to the Array constructor */
            return readNewsArray(reader);

        } finally {
            /** 1st method */
            /** close the reader */
            reader.close();
        }
    }

    public static ArrayList<News> readNewsArray(JsonReader reader) throws IOException {
        /** Create an array to store the newsArticles list */
        ArrayList<News> newsArticles = new ArrayList<News>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("response")) {
                Log.v("QueryUtils REA", "You are inside \"response\" TAG");
                reader.beginObject();
                while (reader.hasNext()) {
                    name = reader.nextName();
                    if (name.equals("results")) {
                        Log.v("QueryUtils RF", "You are inside \"results\" TAG");
                        reader.beginArray();
                        while (reader.hasNext()) {
                            Log.v("QueryUtils RF", "[else] reader.nextName: \"" + name + "\". Reading data...");
                            /** add news object which is under "results" */
                            newsArticles.add(readNews(reader));
                        }
                        reader.endArray();
                    } else {
                        /** skip all values that are not under "results" key */
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                /** skip all values that are not under "response" key on root */
                reader.skipValue();
            }
        }
        Log.v("QueryUtils REA", "No more news to add");
        reader.endObject();
        return newsArticles;
    }

    public static News readNews(JsonReader reader) throws IOException {
        String sectionName = null;
        String webPublicationDate = null;
        String webTitle = null;
        String webUrl = null;
        String authorWebTitle = null;

        Log.v("QueryUtils RE", "You are inside readNews method");

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("sectionName")) {
                /** store String value of key sectionName to variable sectionName */
                sectionName = reader.nextString();
                Log.v("QueryUtils RE", "News Section: " + sectionName);
            } else if (name.equals("webPublicationDate")) {
                /** store String value of key webPublicationDate to variable place */
                webPublicationDate = reader.nextString();
                Log.v("QueryUtils RE", "Publication Date: " + webPublicationDate);
            } else if (name.equals("webTitle")) {
                /** store String value of key webTitle to variable webTitle */
                webTitle = reader.nextString();
                Log.v("QueryUtils RE", "Web Title: " + webTitle);
            } else if (name.equals("webUrl")) {
                /** store string value of key webUrl to variable webUrl */
                webUrl = reader.nextString();
                Log.v("QueryUtils RE", "News article URL: " + webUrl);
            } else if (name.equals("tags")) {
                /** get in tags array, get webTitle for Author name */
                Log.v("QU readNews", "You are inside tags");
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("webTitle")) {
                            authorWebTitle = reader.nextString();
                            Log.v("QueryUtils RE", "News Section TAGS, Name: " + authorWebTitle);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
            } else {
                /** don't do anything with values of other keys */
                reader.skipValue();
            }
        }
        reader.endObject();
        /** Create new News object and return it */
        return new News(webTitle, authorWebTitle, sectionName, webPublicationDate, webUrl);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String assr the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        /** If the URL is null, then return early. */
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                Log.v("makeHttpRequest", "Response code 200 (OK)");
                inputStream = urlConnection.getInputStream();
                jsonResponse = readNewsFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractNewsFromInputStream(String inputStream) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(inputStream));
        try {
            return readNewsArray(reader);
        } finally {
            /** 1st method */
            /** close the reader */
            reader.close();
        }

    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readNewsFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
                Log.v("readFromStream", "New Data Line: " + line);
            }
        }
        Log.v("readFromStream", "News Data: " + output.toString());
        return output.toString();
    }
}