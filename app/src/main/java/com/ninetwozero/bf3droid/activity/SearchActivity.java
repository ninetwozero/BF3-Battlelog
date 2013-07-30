/*
	This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity;

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

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.platoon.PlatoonActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.adapter.SearchDataAdapter;
import com.ninetwozero.bf3droid.datatype.GeneralSearchResult;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.http.WebsiteClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ListActivity {

    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private ListView searchResultList;
    private EditText searchField;
    private Button searchButton;
    private List<GeneralSearchResult> searchResults;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        PublicUtils.restoreCookies(this, icicle);
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.setupLocale(this, sharedPreferences);
        
	setContentView(R.layout.activity_search);

        searchButton = (Button) findViewById(R.id.button_search);
        searchField = (EditText) findViewById(R.id.field_search);

        searchResults = new ArrayList<GeneralSearchResult>();
        setupList(searchResults);
    }

    public void setupList(List<GeneralSearchResult> results) {
        if (searchResultList == null) {
            searchResultList = getListView();
            searchResultList.setChoiceMode(ListView.CHOICE_MODE_NONE);
        }

        if (searchResultList.getAdapter() == null) {
            searchResultList.setAdapter(new SearchDataAdapter(results, layoutInflater));
        } else {
            ((SearchDataAdapter) searchResultList.getAdapter()).setItemArray(results);
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
                    searchField.getText().toString(),
                    BF3Droid.getCheckSum()
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
            this.finish();
        }
        return true;
    }

    public class AsyncForumSearch extends AsyncTask<String, Void, Boolean> {
        private Context context;

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
                searchResults = WebsiteClient.search(context, arg0[0], arg0[1]);
                return searchResults != null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean results) {
            if (results) {
                if (context instanceof SearchActivity) {
                    ((SearchActivity) context).setupList(searchResults);
                    ((SearchActivity) context).toggleSearchButton();
                }
            } else {
                if (context instanceof SearchActivity) {
                    ((SearchActivity) context).toggleSearchButton();
                }
                Toast.makeText(context, R.string.info_xml_generic_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void toggleSearchButton() {
        searchButton.setEnabled(!searchButton.isEnabled());

        if (searchButton.isEnabled()) {
            searchButton.setText(R.string.label_search);
        } else {
            searchButton.setText(R.string.label_search_ongoing);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        Intent intent;
        GeneralSearchResult result = (GeneralSearchResult) v.getTag();

        if (result.hasProfileData()) {
            BF3Droid.setGuest(new User(result.getProfileData().getUsername(), result.getProfileData().getId()));
            intent = new Intent(this, ProfileActivity.class).putExtra("user", User.GUEST);
        } else {
            intent = new Intent(this, PlatoonActivity.class).putExtra("platoon", result.getPlatoonData());
        }
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);
    }
}
