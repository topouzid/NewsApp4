package com.example.android.newsapp4;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> newsArticles) {
        super(context, 0, newsArticles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        /* Check if the existing view is being reused, otherwise inflate the view */
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        /* Get the {@link News} object located at this position in the list */
        News currentNewsArticle = getItem(position);

        /* Find the TextView in the list_item.xml layout with the ID news_title and author_name and set object title to this view */
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.news_title);
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_name);
        String fullArticleName = currentNewsArticle.getNewsTitle();
        String titlePart1;
        String titlePart2;
        /* Split the view if there is a "|" character, otherwise leave part2 empty */
        if (fullArticleName.contains(" | ")) {
            titlePart1 = fullArticleName.substring(0, fullArticleName.indexOf(" | ", 0));
            titlePart2 = fullArticleName.substring(fullArticleName.indexOf(" | ", 0)+3, fullArticleName.length());
        } else {
            titlePart1 = fullArticleName;
            titlePart2 = "";
        }
        titleTextView.setText(titlePart1);
        authorTextView.setText(titlePart2);

        /* Find the Author name if there is one, find TextView in list_item.xml with ID author_name and set name to this view */
        String authorName = currentNewsArticle.getAuthorName();
        if (authorName == null || authorName.equals("")) {
            if (!titlePart2.equals("")) {
                authorTextView.setText(titlePart2);
            } else {
                Log.v("NewsAdapter", "No Author Name under TAGS or title");
            }
        } else {
            authorTextView.setText(authorName);
        }
        /* Find the TextView in the list_item.xml layout with the ID section_name and set object section to this view */
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_name);
        sectionTextView.setText(currentNewsArticle.getSectionName());

        /* Find the TextView in the list_item.xml layout with the ID time_stamp and set object time to this view */
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_stamp);
        timeTextView.setText(currentNewsArticle.getSimpleTimestamp());

        return listItemView;
    }
}
