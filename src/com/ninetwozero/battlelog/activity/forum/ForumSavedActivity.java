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

package com.ninetwozero.battlelog.activity.forum;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.SavedThreadListAdapter;
import com.ninetwozero.battlelog.datatype.ForumThreadData;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;
import com.ninetwozero.battlelog.http.ForumClient;
import com.ninetwozero.battlelog.http.RequestHandler;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.PublicUtils;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ForumSavedActivity extends ListActivity {

    // Attributes
    private SharedPreferences mSharedPreferences;
    private LayoutInflater mLayoutInflater;

    // Elements
    private ListView mListView;

    // Misc
    private List<SavedForumThreadData> mThreads;
    private String mLocale;

    @Override
    public void onCreate(final Bundle icicle) {

        // onCreate - save the instance state
        super.onCreate(icicle);

        // Set sharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore the cookies
        PublicUtils.setupFullscreen(this, mSharedPreferences);
        PublicUtils.restoreCookies(this, icicle);

        // Prepare to tango
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Setup the trinity
        PublicUtils.setupLocale(this, mSharedPreferences);
        PublicUtils.setupSession(this, mSharedPreferences);

        // Set the content view
        setContentView(R.layout.forum_saved_view);

        // Last but not least - init
        initActivity();

    }

    public void initActivity() {

        // Get the ListView
        mListView = getListView();
        registerForContextMenu(mListView);

        // Get the locale
        mLocale = mSharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");
    }

    @Override
    public void onResume() {

        super.onResume();

        // Setup the locale
        PublicUtils.setupLocale(this, mSharedPreferences);

        // Setup the session
        PublicUtils.setupSession(this, mSharedPreferences);

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

                mThreads = CacheHandler.Forum.selectAll(context, SessionKeeper.getProfileData()
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
                mListView
                        .setAdapter(new SavedThreadListAdapter(context, mThreads, mLayoutInflater));

            }

        }

    }

    public class AsyncRefresh extends AsyncTask<SavedForumThreadData, Void, Boolean> {

        // Attributes
        private Context context;
        private View imageBar;
        private ForumThreadData forumThread;

        public AsyncRefresh(Context c, RelativeLayout r) {

            context = c;
            imageBar = r.findViewById(R.id.bar_status);

        }

        @Override
        protected void onPreExecute() {

            imageBar.setBackgroundColor(context.getResources().getColor(R.color.orange));

        }

        @Override
        protected Boolean doInBackground(SavedForumThreadData... t) {

            try {

                // Get the thread
                ForumClient forumHandler = new ForumClient();
                forumHandler.setThreadId(t[0].getId());
                forumThread = forumHandler.getPosts(mLocale);
                boolean status = (forumThread.getNumPosts() > t[0].getNumPosts());

                // Update the saved forum thread
                t[0].setUnread(status);
                t[0].setDateLastPost(forumThread.getLastPostDate());
                t[0].setLastPoster(forumThread.getLastPoster());
                t[0].setDateLastChecked(System.currentTimeMillis() / 1000);

                // Are there new posts?
                CacheHandler.Forum.updateAfterRefresh(context, t[0]);

                return status;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                imageBar.setBackgroundColor(context.getResources().getColor(R.color.green));

            } else {

                imageBar.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));

            }

            // Update the ListView
            ((SavedThreadListAdapter) mListView.getAdapter()).notifyDataSetChanged();

        }

    }

    public class AsyncUpdate extends AsyncTask<SavedForumThreadData, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncUpdate(Context c) {

            context = c;

        }

        @Override
        protected Boolean doInBackground(SavedForumThreadData... t) {

            try {

                // Delete the item
                t[0].setDateLastChecked(System.currentTimeMillis() / 1000);
                t[0].setDateLastRead(System.currentTimeMillis() / 1000);
                t[0].setUnread(false);
                return CacheHandler.Forum.updateAfterView(context, t[0]);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Update the ListView
            reload();

        }

    }

    public class AsyncDelete extends AsyncTask<SavedForumThreadData, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncDelete(Context c) {

            context = c;

        }

        @Override
        protected Boolean doInBackground(SavedForumThreadData... t) {

            try {

                // Delete the item
                return CacheHandler.Forum.delete(context, new long[] {
                        t[0].getId()
                });

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            /* TODO: TOAST? */
            if (result) {

                Toast.makeText(context, "The thread has been removed from the list.",
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(context, "The thread could not be removed from the list.",
                        Toast.LENGTH_SHORT).show();

            }

            // Update the ListView
            reload();

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

            new AsyncUpdate(this).execute(thread);
            startActivity(new Intent(this, ForumActivity.class).putExtra("savedThread", thread));

        } else if (item.getItemId() == 1) {

            new AsyncRefresh(this, (RelativeLayout) info.targetView).execute(thread);

        } else if (item.getItemId() == 2) {

            new AsyncDelete(this).execute(thread);
        }

        return true;
    }

}
