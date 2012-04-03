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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.SavedThreadListAdapter;
import com.ninetwozero.battlelog.datatypes.SavedForumThreadData;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ForumSavedActivity extends ListActivity {

    // Attributes
    private final Context CONTEXT = this;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    // Elements
    private ListView listView;

    // Misc
    private List<SavedForumThreadData> threads;

    @Override
    public void onCreate(Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore the cookies
        PublicUtils.setupFullscreen(this, sharedPreferences);
        PublicUtils.restoreCookies(this, icicle);

        // Prepare to tango
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Setup the trinity
        PublicUtils.setupLocale(this, sharedPreferences);
        PublicUtils.setupSession(this, sharedPreferences);

        // Set the content view
        setContentView(R.layout.forum_saved_view);

        // Last but not least - init
        initActivity();

    }

    public void initActivity() {

        listView = getListView();
        registerForContextMenu(listView);

    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, sharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, sharedPreferences);

        // Reload
        reload();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SUPER_COOKIES,
                RequestHandler.getCookies());

    }

    public void reload() {

        // ASYNC!!!
        new AsyncCache(this).execute();

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
        if (item.getItemId() == R.id.option_reload) {

            this.reload();

        } else if (item.getItemId() == R.id.option_back) {

            ((Activity) this).finish();

        }

        // Return true yo
        return true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return super.onKeyDown(keyCode, event);
    }

    public class AsyncCache extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncCache(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                threads = CacheHandler.Forum.selectAll(context, SessionKeeper.getProfileData()
                        .getId());
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                // Set the adapter
                listView.setAdapter(new SavedThreadListAdapter(context, threads, layoutInflater));

            }

            // Get back here!
            return;

        }

    }

    public class AsyncRefresh extends AsyncTask<Long, Void, Boolean> {

        // Attributes

        public AsyncRefresh() {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Long... arg0) {

            try {

                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                // Siiiiiiiiilent refresh

            } else {

            }

            // Get back here!
            return;

        }

    }

    @Override
    public void onListItemClick(ListView lv, View v, int p, long id) {

        openContextMenu(v);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        menu.add(0, 0, 0, R.string.info_forum_saved_goto);
        menu.add(0, 1, 0, R.string.info_forum_saved_check);
        menu.add(0, 2, 0, R.string.info_forum_saved_remove);

        return;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Declare...
        AdapterView.AdapterContextMenuInfo info;

        // Let's try to get some menu information via a try/catch
        try {

            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        } catch (ClassCastException e) {

            e.printStackTrace();
            return false;

        }

        // Let's get the tag
        SavedForumThreadData thread = (SavedForumThreadData) info.targetView.getTag();

        // Select the correct option
        if (item.getItemId() == 0) {

            startActivity(new Intent(this, ForumActivity.class).putExtra("savedThread", thread));

        } else if (item.getItemId() == 1) {

            Toast.makeText(this, R.string.msg_unimplemented, Toast.LENGTH_SHORT).show();
            // new AsyncRefresh().execute(thread.getId());

        } else if (item.getItemId() == 2) {

            Toast.makeText(this, R.string.msg_unimplemented, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    
}
