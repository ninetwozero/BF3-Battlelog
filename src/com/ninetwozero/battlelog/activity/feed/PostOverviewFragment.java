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
    private Context mContext;

    // Elements
    private TextView mTextTitle;
    private TextView mTextInfo;
    private TextView mTextContent;

    // Misc
    private FeedItem mFeedItem;
    private NewsData mNewsData;
    private boolean mNews = false;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();

        // Let's inflate & return the view
        View view = layoutInflater.inflate(

                mNews ? R.layout.tab_content_post_overview_news
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

        mFeedItem = f;
        mNews = false;
    }

    public void setData(NewsData n) {

        mNewsData = n;
        mNews = true;
    }

    public void initFragment(View v) {

        // Set the values
        if (mNews) {

            initNews(v);

        } else {

            initOther(v);

        }

    }

    public void initNews(View v) {

        mTextTitle = (TextView) v.findViewById(R.id.text_title);
        mTextContent = (TextView) v.findViewById(R.id.text_content);
        mTextInfo = (TextView) v.findViewById(R.id.text_author);

        mTextTitle.setText(mNewsData.getTitle());
        mTextInfo.setText(Html.fromHtml(getString(R.string.info_news_posted_by).replace(
                "{author}",
                mNewsData.getAuthorName()).replace("{date}",
                PublicUtils.getRelativeDate(mContext, mNewsData.getDate()))));
        mTextContent.setText(Html.fromHtml(mNewsData.getContent()));

    }

    public void initOther(View v) {

        mTextTitle = (TextView) v.findViewById(R.id.text_title);
        mTextContent = (TextView) v.findViewById(R.id.text_content);
        mTextInfo = (TextView) v.findViewById(R.id.text_date);

        mTextTitle.setText(Html.fromHtml(mFeedItem.getTitle()));
        mTextInfo.setText(PublicUtils.getRelativeDate(mContext, mFeedItem.getDate()));
        mTextContent.setText(Html.fromHtml(mFeedItem.getContent()));

    }

    public void reload() {
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
