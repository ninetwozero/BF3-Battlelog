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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.platoon.PlatoonActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.UserInfoLoader;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.UserInfoRestorer;
import com.ninetwozero.bf3droid.adapter.SearchDataAdapter;
import com.ninetwozero.bf3droid.datatype.GeneralSearchResult;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.http.RequestHandler;
import com.ninetwozero.bf3droid.http.WebsiteClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragmentActivity extends Bf3FragmentActivity implements UserInfoLoader.Callback {

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
        searchResultList = (ListView) findViewById(R.id.search_list);
        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GeneralSearchResult result = (GeneralSearchResult) view.getTag();

                if (result.hasProfileData()) {
                    BF3Droid.setGuest(new User(result.getProfileData().getUsername(), result.getProfileData().getId()));
                    startUserInfoLoader();
                } else {
                    Intent intent = new Intent(getApplicationContext(), PlatoonActivity.class).putExtra("platoon", result.getPlatoonData());
                    startActivity(intent);
                }
            }
        });
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
            if (context instanceof SearchFragmentActivity) {
                ((SearchFragmentActivity) context).toggleSearchButton();
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
            //emptyListViewVisibility();
            if (results) {
                if (context instanceof SearchFragmentActivity) {
                    searchResultList.setAdapter(new SearchDataAdapter(searchResults, layoutInflater));
                    ((SearchFragmentActivity) context).toggleSearchButton();
                }
            } else {
                if (context instanceof SearchFragmentActivity) {
                    ((SearchFragmentActivity) context).toggleSearchButton();
                }
                Toast.makeText(context, R.string.info_xml_generic_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*private void emptyListViewVisibility(){
        if(searchResults.size() == 0){
            TextView emptyView = (TextView)findViewById(android.R.id.empty);
            searchResultList.setEmptyView(emptyView);
        } else {
            TextView emptyView = (TextView) searchResultList.getEmptyView();
            emptyView.setVisibility(View.GONE);
        }
    }*/

    public void toggleSearchButton() {
        searchButton.setEnabled(!searchButton.isEnabled());

        if (searchButton.isEnabled()) {
            searchButton.setText(R.string.label_search);
        } else {
            searchButton.setText(R.string.label_search_ongoing);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);
    }

    private void startUserInfoLoader() {
        startLoadingDialog(SearchFragmentActivity.class.getSimpleName());
        new UserInfoLoader(this, getApplicationContext(), User.GUEST, getSupportLoaderManager()).restart();
    }

    @Override
    public void onLoadFinished(UserInfo userInfo) {
        saveToApp(userInfo);
        new UserInfoRestorer(getApplicationContext(), User.GUEST).save(userInfo);
        closeLoadingDialog(SearchFragmentActivity.class.getSimpleName());
        startProfileActivity();
    }

    private void saveToApp(UserInfo userInfo) {
        BF3Droid.getUserBy(User.GUEST).setPersonas(userInfo.getPersonas());
        BF3Droid.getUserBy(User.GUEST).setPlatoons(userInfo.getPlatoons());
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class).putExtra("user", User.GUEST);
        startActivity(intent);
    }
}
