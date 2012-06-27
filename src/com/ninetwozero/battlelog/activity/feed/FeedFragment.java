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

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.news.SinglePostActivity;
import com.ninetwozero.battlelog.adapter.FeedListAdapter;
import com.ninetwozero.battlelog.asynctask.AsyncFeedHooah;
import com.ninetwozero.battlelog.asynctask.AsyncPostToWall;
import com.ninetwozero.battlelog.asynctask.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.FeedClient;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class FeedFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Elements
    private ListView mListView;
    private FeedListAdapter mListAdapter;
    private EditText mFieldMessage;
    private TextView mTextTitle;
    private RelativeLayout mWrapInput;
    private Button mButtonSend;

    // Misc
    private List<FeedItem> mFeedItems;
    private SharedPreferences mSharedPreferences;
    private String mTitle;
    private int mType;
    private long mId;
    private boolean mWrite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_feed,
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
        mWrapInput = (RelativeLayout) v.findViewById(R.id.wrap_input);
        mListView = (ListView) v.findViewById(android.R.id.list);
        mFieldMessage = (EditText) v.findViewById(R.id.field_message);
        mTextTitle = (TextView) v.findViewById(R.id.text_title);
        mButtonSend = (Button) v.findViewById(R.id.button_send);

        // Setup the listAdapter
        mListAdapter = new FeedListAdapter(mContext, mFeedItems,
                mLayoutInflater);
        mListView.setAdapter(mListAdapter);

        // Handle the *type*-specific events here
        if (mType == FeedClient.TYPE_GLOBAL) {

            mTextTitle.setText(R.string.info_feed_title_global);
            mFieldMessage.setHint(R.string.info_xml_hint_status);
            mWrapInput.setVisibility(mWrite ? View.VISIBLE : View.GONE);

        } else if (mType == FeedClient.TYPE_PROFILE) {

            mTextTitle.setText(mTitle);
            mFieldMessage.setHint(R.string.info_xml_hint_feed);
            mWrapInput.setVisibility(mWrite ? View.VISIBLE : View.GONE);

        } else if (mType == FeedClient.TYPE_PLATOON) {

            mTextTitle.setText(mTitle);
            mFieldMessage.setHint(R.string.info_xml_hint_feed);
            mWrapInput.setVisibility(mWrite ? View.VISIBLE : View.GONE);

        }

        // Setup the button click
        mButtonSend.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        // Empty message?
                        String message = mFieldMessage.getText().toString();
                        if ("".equals(message)) {

                            Toast.makeText(mContext, R.string.info_empty_msg,
                                    Toast.LENGTH_SHORT).show();

                        }

                        // Let's do it accordingly
                        if (mType == FeedClient.TYPE_GLOBAL) {

                            new AsyncStatusUpdate(mContext, FeedFragment.this).execute(message,
                                    mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM,
                                            ""));

                        } else if (mType == FeedClient.TYPE_PROFILE) {

                            new AsyncPostToWall(

                                    mContext, mId, false, FeedFragment.this

                            ).execute(

                                    mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM,
                                            ""),
                                    message

                                    );

                        } else if (mType == FeedClient.TYPE_PLATOON) {

                            new AsyncPostToWall(

                                    mContext, mId, true, FeedFragment.this

                            ).execute(

                                    mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM,
                                            ""),
                                    message

                                    );

                        }

                        // Empty the field
                        mFieldMessage.setText("");

                    }
                }
                );

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());

    }

    public void reload() {

        // Feed refresh!
        new AsyncRefresh(

                mContext, SessionKeeper.getProfileData().getId()

        ).execute();

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        getActivity().openContextMenu(v);

    }

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Get the actual menu item and tag
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        // Show the menu
        FeedItem feedItem = (FeedItem) info.targetView.getTag();
        menu.add(Constants.MENU_ID_FEED, 0, 0,
                feedItem.isLiked() ? R.string.label_hooah : R.string.label_unhooah);
        menu.add(Constants.MENU_ID_FEED, 1, 0, R.string.label_single_post_view);

        // Platoon feeds only have posts that would open a new platoon activity
        if (mType != FeedClient.TYPE_PLATOON) {
            menu.add(Constants.MENU_ID_FEED, 2, 0, R.string.label_goto_item);
        }

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        try {

            // Grab the data
            FeedItem feedItem = (FeedItem) info.targetView.getTag();

            // REQUESTS
            if (item.getItemId() == 0) {

                new AsyncFeedHooah(mContext, info.id, false, feedItem.isLiked(), this)
                        .execute(mSharedPreferences.getString(
                                Constants.SP_BL_PROFILE_CHECKSUM, ""));

            } else if (item.getItemId() == 1) {

                // Yeah
                startActivity(

                new Intent(

                        mContext, SinglePostActivity.class

                ).putExtra(

                        "feed", feedItem

                        ).putExtra(

                                "canComment", mWrite

                        )

                );

            } else if (item.getItemId() == 2) {

                if (feedItem.getIntent(mContext) != null) {
                    startActivity(feedItem.getIntent(mContext));
                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

        return true;

    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private final Context context;
        private final long activeProfileId;

        public AsyncRefresh(Context c, long pId) {

            this.context = c;
            this.activeProfileId = pId;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                mFeedItems = new FeedClient(mId, mType).get(

                        context, mSharedPreferences.getInt(Constants.SP_BL_NUM_FEED,
                                Constants.DEFAULT_NUM_FEED), activeProfileId

                        );

                // ...validate!
                return (mFeedItems != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                Toast.makeText(this.context, R.string.info_feed_empty,
                        Toast.LENGTH_SHORT).show();

            }

            // Update
            mListAdapter.setItemArray(mFeedItems);

        }

    }

    public void setTitle(String t) {

        mTitle = t;

    }

    public void setType(int t) {

        mType = t;

    }

    public int getType() {

        return mType;
    }

    public void setId(long i) {

        mId = i;

    }

    public void setCanWrite(boolean c) {

        mWrite = c;
        if (mWrapInput != null) {
            mWrapInput.setVisibility(mWrite ? View.VISIBLE : View.GONE);
        }

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
