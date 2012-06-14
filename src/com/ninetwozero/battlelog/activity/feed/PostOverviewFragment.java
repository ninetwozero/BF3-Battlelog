/*
    This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.activity.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.NewsData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class PostOverviewFragment extends Fragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;

    // Elements
    private TextView textTitle, textInfo, textContent;

    // Misc
    private FeedItem feedItem;
    private NewsData newsData;
    private boolean isNews = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(

                isNews ? R.layout.tab_content_post_overview_news
                        : R.layout.tab_content_post_overview_feed,

                container, false);

        // Init
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        reload();

    }

    public void setData(FeedItem f) {

        feedItem = f;
        isNews = false;
    }

    public void setData(NewsData n) {

        newsData = n;
        isNews = true;
    }

    public void initFragment(View v) {

        // Set the values
        if (isNews) {

            initNews(v);

        } else {

            initOther(v);

        }

    }

    public void initNews(View v) {


        textTitle = (TextView) v.findViewById(R.id.text_title);
        textContent = (TextView) v.findViewById(R.id.text_content);
        textInfo = (TextView) v.findViewById(R.id.text_author);
        
        textTitle.setText(newsData.getTitle());
        textInfo.setText(Html.fromHtml(getString(R.string.info_news_posted_by).replace(
                "{author}",
                newsData.getAuthorName()).replace("{date}",
                PublicUtils.getRelativeDate(context, newsData.getDate()))));
        textContent.setText(Html.fromHtml(newsData.getContent()));

    }

    public void initOther(View v) {

        textTitle = (TextView) v.findViewById(R.id.text_title);
        textContent = (TextView) v.findViewById(R.id.text_content);
        textInfo = (TextView) v.findViewById(R.id.text_date);
        
        textTitle.setText(Html.fromHtml(feedItem.getTitle()));
        textInfo.setText(PublicUtils.getRelativeDate(context, feedItem.getDate()));
        textContent.setText(Html.fromHtml(feedItem.getContent()));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    public void reload() {
    }

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        return;

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        return false;

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }
}
