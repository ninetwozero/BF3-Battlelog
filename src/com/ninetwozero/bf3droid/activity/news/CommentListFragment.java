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

package com.ninetwozero.bf3droid.activity.news;

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

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.CommentListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncCommentSend;
import com.ninetwozero.bf3droid.datatype.CommentData;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import com.ninetwozero.bf3droid.http.CommentClient;
import com.ninetwozero.bf3droid.misc.Constants;

public class CommentListFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;
    private long mId;
    private int mType;

    // Elements
    private ListView mListView;
    private Button mButton;
    private CommentListAdapter mListAdapter;
    private EditText mFieldMessage;

    // Misc
    private List<CommentData> mComments;
    private int mPageId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_comment, container, false);
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    public void initFragment(View v) {
        mListView = (ListView) v.findViewById(android.R.id.list);
        mButton = (Button) v.findViewById(R.id.button_send);
        mFieldMessage = (EditText) v.findViewById(R.id.field_message);

        mButton.setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncCommentSend(mContext, mId, mType, mButton).execute(
                        mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""),
                        mFieldMessage.getText().toString()
                    );
                    mFieldMessage.setText("");
                }
            }
		);
        mListAdapter = new CommentListAdapter(mContext, mComments, mLayoutInflater);
        mListView.setAdapter(mListAdapter);
    }

    public void setId(long i) {
        mId = i;
    }

    public void setType(int t) {
        mType = t;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void reload() {
        new AsyncRefresh(mContext).execute();
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Toast.makeText(mContext, "CLICK!", Toast.LENGTH_SHORT).show();
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
                mComments = new CommentClient(mId, mType).get(mPageId);
                return (mComments != null);
            } catch (WebsiteHandlerException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(this.context, R.string.general_no_data, Toast.LENGTH_SHORT).show();
            }
            mListAdapter.setItemArray(mComments);
        }
    }

    public void setPageId(int s) {
        mPageId = s;
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
