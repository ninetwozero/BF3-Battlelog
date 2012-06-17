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

package com.ninetwozero.battlelog.activity.news;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.CommentListAdapter;
import com.ninetwozero.battlelog.asynctask.AsyncCommentSend;
import com.ninetwozero.battlelog.datatype.CommentData;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.CommentClient;
import com.ninetwozero.battlelog.misc.Constants;

public class CommentListFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private long id;
    private int type;

    // Elements
    private ListView listView;
    private Button button;
    private CommentListAdapter listAdapter;
    private EditText fieldMessage;

    // Misc
    private List<CommentData> comments;
    private int pageId = 1;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_comment,
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
        button = (Button) v.findViewById(R.id.button_send);
        fieldMessage = (EditText) v.findViewById(R.id.field_message);

        // Set the click listener
        button.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        new AsyncCommentSend(context, id, type,
                                button).execute(

                                sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
                                fieldMessage.getText().toString()

                                );

                        // Clear the field
                        fieldMessage.setText("");

                    }

                }

                );

        // Setup the listAdapter
        listAdapter = new CommentListAdapter(context, comments,
                layoutInflater);
        listView.setAdapter(listAdapter);

    }

    public void setId(long i) {

        id = i;

    }

    public void setType(int t) {

        type = t;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    public void reload() {

        // Feed refresh!
        new AsyncRefresh(context).execute();

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        Toast.makeText(context, "CLICK!", Toast.LENGTH_SHORT).show();

    }

    public class AsyncRefresh extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncRefresh(Context c) {

            this.context = c;

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                // Get...
                comments = new CommentClient(id, type).get(pageId);

                // ...validate!
                return (comments != null);

            } catch (WebsiteHandlerException ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            // Fail?
            if (!result) {

                Toast.makeText(this.context, R.string.general_no_data,
                        Toast.LENGTH_SHORT).show();

            }

            // Update
            listAdapter.setItemArray(comments);

        }

    }

    public void setPageId(int s) {

        pageId = s;

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