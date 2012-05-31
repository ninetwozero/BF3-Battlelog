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

package com.ninetwozero.battlelog.fragments;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.SingleNewsActivity;
import com.ninetwozero.battlelog.adapters.NewsListAdapter;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.handlers.WebsiteHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class NewsListFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;

    // Elements
    private ListView listView;
    private NewsListAdapter newsListAdapter;

    // Misc
    private List<NewsData> newsItems;
    private int start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_news,
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

    public void initFragment(View v) {

        // Get the elements
        listView = (ListView) v.findViewById(android.R.id.list);

        // Setup the listAdapter
        newsListAdapter = new NewsListAdapter(context, newsItems,
                layoutInflater);
        listView.setAdapter(newsListAdapter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    public void reload() {

        // Feed refresh!
        new AsyncFeedRefresh(

                context, SessionKeeper.getProfileData().getId()

        ).execute();

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        context.startActivity(new Intent(context, SingleNewsActivity.class).putExtra("news",
                (NewsData) v.getTag()));

    }

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        return;

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        return false;

    }

    public class AsyncFeedRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncFeedRefresh(Context c, long pId) {

            this.context = c;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                newsItems = new WebsiteHandler().getNewsForPage(start);

                // ...validate!
                return (newsItems != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                Toast.makeText(this.context, R.string.info_news_empty,
                        Toast.LENGTH_SHORT).show();
                return;

            }

            // Update
            newsListAdapter.setItemArray(newsItems);

            // Get back here!
            return;

        }

    }

    public void setStart(int s) {

        start = s;

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
