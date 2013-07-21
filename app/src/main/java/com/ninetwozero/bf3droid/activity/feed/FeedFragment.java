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

package com.ninetwozero.bf3droid.activity.feed;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.news.SinglePostActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.bf3droid.adapter.FeedListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncFeedHooah;
import com.ninetwozero.bf3droid.asynctask.AsyncPostToWall;
import com.ninetwozero.bf3droid.asynctask.AsyncStatusUpdate;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.FeedItem;
import com.ninetwozero.bf3droid.datatype.ProfileData;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.dialog.HooahListDialogFragment;
import com.ninetwozero.bf3droid.dialog.OnCloseProfileListDialogListener;
import com.ninetwozero.bf3droid.http.FeedClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.model.User;

import java.util.List;

public class FeedFragment extends ListFragment implements DefaultFragment, OnCloseProfileListDialogListener/*, LoaderManager.LoaderCallbacks<CompletedTask>*/ {
    private Context context;
    private LayoutInflater layoutInflater;

    private ListView listView;
    private FeedListAdapter listAdapter;
    private EditText message;
    private TextView title;
    private RelativeLayout wrapInput;
    private Button send;

    private long id = 0;
    private int feedType;
    private String titleText;
    private boolean isWritable;
    private List<FeedItem> feedItems;
    private Bundle bundle;

    private final int FEED_ACTION = 30;
    public final static int CONTEXT_ID_HOOAH = 0;
    public final static int CONTEXT_ID_SINGLE = 1;
    public final static int CONTEXT_ID_VIEW_HOOAH = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;
        this.bundle = bundle;

        View view = layoutInflater.inflate(R.layout.tab_content_feed, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (id > 0 || feedType == FeedClient.TYPE_GLOBAL) {
            reload();
        }
    }

    public void initFragment(View v) {
        wrapInput = (RelativeLayout) v.findViewById(R.id.wrap_input);
        listView = (ListView) v.findViewById(android.R.id.list);
        message = (EditText) v.findViewById(R.id.field_message);
        title = (TextView) v.findViewById(R.id.text_title);
        send = (Button) v.findViewById(R.id.button_send);

        listAdapter = new FeedListAdapter(context, feedItems, layoutInflater);
        listView.setAdapter(listAdapter);

        if (feedType == FeedClient.TYPE_GLOBAL) {
            title.setText(R.string.info_feed_title_global);
            message.setHint(R.string.info_xml_hint_status);
            wrapInput.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        } else if (feedType == FeedClient.TYPE_PROFILE) {
            title.setText(titleText);
            message.setHint(R.string.info_xml_hint_feed);
            wrapInput.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        } else if (feedType == FeedClient.TYPE_PLATOON) {
            title.setText(titleText);
            message.setHint(R.string.info_xml_hint_feed);
            wrapInput.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        } else {
            title.setText(R.string.info_feed_title_global);
            message.setHint(R.string.info_xml_hint_status);
            wrapInput.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        }

        send.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = FeedFragment.this.message.getText().toString();
                        if ("".equals(message)) {
                            Toast.makeText(context, R.string.info_empty_msg,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (feedType == FeedClient.TYPE_GLOBAL) {
                            new AsyncStatusUpdate(context, FeedFragment.this).execute(
                                    message, checksum());
                        } else if (feedType == FeedClient.TYPE_PROFILE) {
                            new AsyncPostToWall(context, id, false, FeedFragment.this)
                                    .execute(checksum(), message);
                        } else if (feedType == FeedClient.TYPE_PLATOON) {
                            new AsyncPostToWall(context, id, true, FeedFragment.this)
                                    .execute(checksum(), message);
                        }
                        FeedFragment.this.message.setText("");
                    }
                }
        );
    }

    private String checksum() {
        return BF3Droid.getCheckSum();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    public void reload() {
        new AsyncRefresh(context, feedQueryId(), personaId()).execute();
        //getLoaderManager().restartLoader(FEED_ACTION, bundle, this);
    }

    private long feedQueryId() {
        return feedType == FeedClient.TYPE_PLATOON ? platoonId() : user().getId();
    }

    /*@Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {
        return new Bf3Loader(getContext(), httpData());
    }

    private Bf3ServerCall.HttpData httpData() {
        return new Bf3ServerCall.HttpData(UriFactory.userFeeds(), HttpGet.METHOD_NAME);
    }*/

    private Context getContext() {
        return getActivity().getApplicationContext();
    }

    /*@Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
        if (completedTask.result == CompletedTask.Result.SUCCESS) {
            String response = completedTask.response;
        }
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {
    }*/

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        getActivity().openContextMenu(v);
    }

    private long userId(){
        return user().getId();
    }

    private long personaId(){
        return user().selectedPersona().getPersonaId();
    }

    private User user() {
        if (getArguments() != null && getArguments().containsKey("user")) {
            return BF3Droid.getUserBy(getArguments().getString("user"));
        } else {
            return BF3Droid.getUser();
        }
    }

    private long platoonId(){
        return user().selectedPlatoon().getPlatoonId();
    }

    public void createContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        FeedItem feedItem = (FeedItem) info.targetView.getTag();

        menu.add(Constants.MENU_ID_FEED, CONTEXT_ID_HOOAH, 0, feedItem
                .isLiked() ? R.string.label_unhooah : R.string.label_hooah);

        if (feedItem.getNumLikes() > 0) {
            menu.add(Constants.MENU_ID_FEED, CONTEXT_ID_VIEW_HOOAH, 0, "View hooahs");
        }

        menu.add(Constants.MENU_ID_FEED, CONTEXT_ID_SINGLE, 0,
                R.string.label_single_post_view);
    }

    public boolean handleSelectedContextItem(
            AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        try {
            FeedItem feedItem = (FeedItem) info.targetView.getTag();

            if (item.getItemId() == CONTEXT_ID_HOOAH) {
                new AsyncFeedHooah(context, info.id, false, feedItem.isLiked(), this).execute(checksum());
            } else if (item.getItemId() == CONTEXT_ID_SINGLE) {
                startActivity(new Intent(context, SinglePostActivity.class)
                        .putExtra("feed", feedItem).putExtra("canComment", isWritable));
            } else if (item.getItemId() == CONTEXT_ID_VIEW_HOOAH) {
                FragmentManager manager = getFragmentManager();
                HooahListDialogFragment dialog = HooahListDialogFragment.newInstance(feedItem, getTag());
                dialog.show(manager, "profile_dialog");
            }
        } catch (Exception ex) {
            Log.i("FeedFragment", ex.toString());
            return false;
        }
        return true;
    }

    private class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        private final Context context;
        private final long userId;
        private final long personaId;

        public AsyncRefresh(Context c, long userId, long personaId) {
            this.context = c;
            this.userId = userId;
            this.personaId = personaId;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                feedItems = new FeedClient(userId, feedType).get(context, Constants.DEFAULT_NUM_FEED, personaId);
                return (feedItems != null);
            } catch (WebsiteHandlerException ex) {
                Log.i("FeedFragment", ex.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(this.context, R.string.info_feed_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            listAdapter.setItems(feedItems);
        }
    }

    public void setTitle(String t) {
        titleText = t;
    }

    public void setType(int t) {
        feedType = t;
    }

    public int getType() {
        return feedType;
    }

    public void setId(long i) {
        id = i;
    }

    public void setCanWrite(boolean c) {
        isWritable = c;
        if (wrapInput != null) {
            wrapInput.setVisibility(isWritable ? View.VISIBLE : View.GONE);
        }
    }

    public void prePostMode() {
        send.setEnabled(false);
    }

    public void postPostMode() {
        send.setEnabled(true);
        message.setText("");
    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

    @Override
    public void onDialogListSelection(ProfileData profile) {
        startActivity(new Intent(context, ProfileActivity.class).putExtra("profile", profile));
    }
}
