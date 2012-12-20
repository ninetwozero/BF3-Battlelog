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

package com.ninetwozero.bf3droid.activity.news;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.NewsListAdapter;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.NewsData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.WebsiteClient;

public class NewsListFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ListView mListView;
    private NewsListAdapter mNewsListAdapter;

    private List<NewsData> mNewsItems;
    private int mStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_dashboard_news, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    public void initFragment(View v) {
        mListView = (ListView) v.findViewById(android.R.id.list);
        mNewsListAdapter = new NewsListAdapter(mContext, mNewsItems, mLayoutInflater);
        mListView.setAdapter(mNewsListAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void reload() {
        new AsyncFeedRefresh(mContext).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        mContext.startActivity(
            new Intent(mContext, SinglePostActivity.class).putExtra(
                    "news", (NewsData) v.getTag()
            )
        );
    }

    public class AsyncFeedRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncFeedRefresh(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                mNewsItems = new WebsiteClient().getNewsForPage(mStart);
                return (mNewsItems != null);
            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(this.context, R.string.info_news_empty, Toast.LENGTH_SHORT).show();
            } else {
            	mNewsListAdapter.setItemArray(mNewsItems);
            }
        }
    }

    public void setStart(int s) {
        mStart = s;
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
