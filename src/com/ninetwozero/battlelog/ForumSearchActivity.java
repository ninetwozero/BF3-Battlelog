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

package com.ninetwozero.battlelog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ForumSearchAdapter;
import com.ninetwozero.battlelog.datatypes.ForumSearchResult;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumSearchActivity extends ListActivity {

    // Attributes
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;

    // Elements
    private ListView listView;
    private EditText fieldSearch;
    private Button buttonSearch;

    // Misc
    private List<ForumSearchResult> threads;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PublicUtils.restoreCookies(this, icicle);

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.forum_search_view);

        // Prepare to tango
        this.layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the elements
        buttonSearch = (Button) findViewById(R.id.button_search);
        fieldSearch = (EditText) findViewById(R.id.field_search);

        // Threads!
        threads = new ArrayList<ForumSearchResult>();
        setupList(threads);
    }

    public void setupList(List<ForumSearchResult> results) {

        // Do we have it?
        if (listView == null) {

            // Get the ListView
            listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        }

        // Does it have an adapter?
        if (listView.getAdapter() == null) {

            listView.setAdapter(new ForumSearchAdapter(this, results,
                    layoutInflater));

        } else {

            ((ForumSearchAdapter) listView.getAdapter()).setItemArray(results);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {

        // Send?
        if (v.getId() == R.id.button_search) {

            // Send it!
            new AsyncForumSearch(this)
                    .execute(fieldSearch.getText().toString());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_basic, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Let's act!
        if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    public class AsyncForumSearch extends AsyncTask<String, Void, Boolean> {

        // Attributes
        private Context context;

        // Construct
        public AsyncForumSearch(Context c) {
            this.context = c;
        }

        @Override
        protected void onPreExecute() {

            if (context instanceof ForumSearchActivity) {

                ((ForumSearchActivity) context).toggleSearchButton();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                threads = WebsiteHandler.searchInForums(context, arg0[0]);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            // Let's evaluate
            if (results) {

                if (context instanceof ForumSearchActivity) {

                    ((ForumSearchActivity) context).setupList(threads);
                    ((ForumSearchActivity) context).toggleSearchButton();

                }

            } else {

                if (context instanceof ForumSearchActivity) {

                    ((ForumSearchActivity) context).toggleSearchButton();

                }
                Toast.makeText(context, R.string.info_xml_generic_error,
                        Toast.LENGTH_SHORT).show();

            }

        }

    }

    public void toggleSearchButton() {

        buttonSearch.setEnabled(!buttonSearch.isEnabled());

        // Update the text
        if (buttonSearch.isEnabled()) {
            buttonSearch.setText(R.string.label_search);
        } else {
            buttonSearch.setText("...");
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {

        startActivity(

        new Intent(this, Backup_ForumThreadView.class).putExtra(

                "threadId", id

                ).putExtra(

                        "threadTitle", ((ForumSearchResult) v.getTag()).getTitle()

                )

        );

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

    }

}
