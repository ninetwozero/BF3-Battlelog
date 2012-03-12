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
import com.ninetwozero.battlelog.SinglePostView;
import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncFeedHooah;
import com.ninetwozero.battlelog.asynctasks.AsyncPostToWall;
import com.ninetwozero.battlelog.asynctasks.AsyncStatusUpdate;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class FeedFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;

    // Elements
    private ListView listFeed;
    private FeedListAdapter feedListAdapter;
    private EditText fieldMessage;
    private TextView textTitle;
    private RelativeLayout wrapInput;
    private Button buttonSend;

    // Misc
    private List<FeedItem> feedItems;
    private SharedPreferences sharedPreferences;
    private String title;
    private int type;
    private long id;
    private boolean canWrite;

    // Constants
    public final static int TYPE_GLOBAL = 0;
    public final static int TYPE_PROFILE = 1;
    public final static int TYPE_PLATOON = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_feed,
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
        wrapInput = (RelativeLayout) v.findViewById(R.id.wrap_input);
        listFeed = (ListView) v.findViewById(android.R.id.list);
        fieldMessage = (EditText) v.findViewById(R.id.field_message);
        textTitle = (TextView) v.findViewById(R.id.text_title);
        buttonSend = (Button) v.findViewById( R.id.button_send );
        
        // Setup the listAdapter
        feedListAdapter = new FeedListAdapter(context, feedItems,
                layoutInflater);
        listFeed.setAdapter(feedListAdapter);

        // Handle the *type*-specific events here
        if (type == FeedFragment.TYPE_GLOBAL) {

            textTitle.setText(R.string.info_feed_title_global);
            fieldMessage.setHint(R.string.info_xml_hint_status);
            wrapInput.setVisibility(canWrite ? View.VISIBLE : View.GONE);

        } else if (type == FeedFragment.TYPE_PROFILE) {

            textTitle.setText(title);
            fieldMessage.setHint(R.string.info_xml_hint_feed);
            wrapInput.setVisibility(canWrite ? View.VISIBLE : View.GONE);

        } else if (type == FeedFragment.TYPE_PLATOON) {

            textTitle.setText(title);
            fieldMessage.setHint(R.string.info_xml_hint_feed);
            wrapInput.setVisibility(canWrite ? View.VISIBLE : View.GONE);
       
        }
        
        //Setup the button click 
        buttonSend.setOnClickListener( 
                
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        
                        // Empty message?
                        String message = fieldMessage.getText().toString();
                        if (message.equals("")) {

                            Toast.makeText(context, R.string.info_empty_msg,
                                    Toast.LENGTH_SHORT).show();
                            return;

                        }

                        //Let's do it accordingly
                        if( type == FeedFragment.TYPE_GLOBAL ) {

                            new AsyncStatusUpdate(context, FeedFragment.this).execute(message, sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ));
                        
                        } else if( type == FeedFragment.TYPE_PROFILE ) {

                            new AsyncPostToWall(
                                    
                                    context, id, false, FeedFragment.this
    
                            ).execute(
    
                                    sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""),
                                    message

                            );                       
                            
                        } else if( type == FeedFragment.TYPE_PLATOON ) {
                          
                            new AsyncPostToWall(
                                    
                                    context, id, true, FeedFragment.this
    
                            ).execute(
    
                                    sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""),
                                    message
    
                                    ); 
                            
                        }
                        

                        //Empty the field
                        fieldMessage.setText("");
                        
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
        new AsyncFeedRefresh(

                context, SessionKeeper.getProfileData().getProfileId()

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
        if (!((FeedItem) ((View) info.targetView).getTag()).isLiked()) {

            menu.add(Constants.MENU_ID_FEED, 0, 0, R.string.label_hooah);

        } else {

            menu.add(Constants.MENU_ID_FEED, 0, 0, R.string.label_unhooah);

        }
        menu.add(Constants.MENU_ID_FEED, 1, 0, R.string.label_single_post_view);
        if( type != FeedFragment.TYPE_PLATOON ) { menu.add(Constants.MENU_ID_FEED, 2, 0, R.string.label_goto_item); }

        return;

    }
    
    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {
        
        try {

            // Grab the data
            FeedItem feedItem = (FeedItem) info.targetView.getTag();

            // REQUESTS
            if (item.getItemId() == 0) {

                new AsyncFeedHooah(context,info.id, false, feedItem.isLiked(), this)
                        .execute(sharedPreferences.getString(
                                Constants.SP_BL_CHECKSUM, ""));
                 

            } else if (item.getItemId() == 1) {

                // Yeah
                startActivity(

                new Intent(

                        context, SinglePostView.class

                ).putExtra(

                        "feedItem", feedItem

                        ).putExtra(

                                "canComment", canWrite

                        ).putExtra(

                                "profileId", feedItem.getProfile(0).getProfileId()

                        )

                );

            } else if (item.getItemId() == 2) {

                if (feedItem.getIntent(context) != null) {
                    startActivity(feedItem.getIntent(context));
                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }
        
        return true;
        
    }

    public class AsyncFeedRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;
        private long activeProfileId;

        public AsyncFeedRefresh(Context c, long pId) {

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
                feedItems = WebsiteHandler.getFeed(

                        context, type, id, sharedPreferences.getInt(Constants.SP_BL_NUM_FEED,
                                Constants.DEFAULT_NUM_FEED), activeProfileId

                        );

                // ...validate!
                return (feedItems != null);

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
                return;

            }

            // Update
            feedListAdapter.setItemArray(feedItems);

            // Get back here!
            return;

        }

    }

    public void setTitle(String t) {

        title = t;

    }

    public void setType(int t) {

        type = t;

    }
    
    public int getType() {
        
        return type;
    }

    public void setId(long i) {

        id = i;

    }

    public void setCanWrite(boolean c) {

        canWrite = c;
        if (wrapInput != null) {
            wrapInput.setVisibility(canWrite ? View.VISIBLE : View.GONE);
        }

    }
    
    @Override
    public Menu prepareOptionsMenu(Menu menu) { return menu; }

    @Override
    public boolean handleSelectedOption(MenuItem item) { return false; }
}
