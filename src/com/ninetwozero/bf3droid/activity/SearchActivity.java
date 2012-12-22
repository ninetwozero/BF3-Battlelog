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

package com.ninetwozero.bf3droid.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.platoon.PlatoonActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.adapter.SearchDataAdapter;
import com.ninetwozero.bf3droid.datatype.GeneralSearchResult;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.http.WebsiteClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ListActivity {

    // Attributes
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;

    // Elements
    private ListView mListView;
    private EditText mFieldSearch;
    private Button mButtonSearch;

    // Misc
    private List<GeneralSearchResult> mSearchResults;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.setupLocale(this, mSharedPreferences);
        
	setContentView(R.layout.activity_search);

        mButtonSearch = (Button) findViewById(R.id.button_search);
        mFieldSearch = (EditText) findViewById(R.id.field_search);

        mSearchResults = new ArrayList<GeneralSearchResult>();
        setupList(mSearchResults);
    }

    public void setupList(List<GeneralSearchResult> results) {
        if (mListView == null) {
            mListView = getListView();
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        }

        if (mListView.getAdapter() == null) {
            mListView.setAdapter(new SearchDataAdapter(results, mLayoutInflater));
        } else {
            ((SearchDataAdapter) mListView.getAdapter()).setItemArray(results);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_search) {

            new AsyncForumSearch(this).execute(
                mFieldSearch.getText().toString(),
                mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")
            );
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
        if (item.getItemId() == R.id.option_back) {
            ((Activity) this).finish();
        }
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
            if (context instanceof SearchActivity) {
                ((SearchActivity) context).toggleSearchButton();
            }
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                mSearchResults = WebsiteClient.search(context, arg0[0], arg0[1]);
                return mSearchResults != null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            if (results) {
                if (context instanceof SearchActivity) {
                    ((SearchActivity) context).setupList(mSearchResults);
                    ((SearchActivity) context).toggleSearchButton();
                }
            } else {
                if (context instanceof SearchActivity) {
                    ((SearchActivity) context).toggleSearchButton();
                }
                Toast.makeText(context, R.string.info_xml_generic_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toggleSearchButton() {
        mButtonSearch.setEnabled(!mButtonSearch.isEnabled());

        // Update the text
        if (mButtonSearch.isEnabled()) {
            mButtonSearch.setText(R.string.label_search);
        } else {
            mButtonSearch.setText(R.string.label_search_ongoing);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        Intent intent = null;
        GeneralSearchResult result = (GeneralSearchResult) v.getTag();

        // Build the intent
        if (result.hasProfileData()) {
            intent = new Intent(this, ProfileActivity.class).putExtra("profile", result.getProfileData());
        } else {
            intent = new Intent(this, PlatoonActivity.class).putExtra("platoon", result.getPlatoonData());
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);
    }
}
