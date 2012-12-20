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

package com.ninetwozero.bf3droid.activity.forum;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.ForumListAdapter;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.ForumData;
import com.ninetwozero.bf3droid.http.ForumClient;
import com.ninetwozero.bf3droid.misc.Constants;
import com.ninetwozero.bf3droid.misc.DataBank;

public class BoardFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Elements
    private ListView mListView;
    private TextView mTextTitle;

    // Misc
    private String mLocale;
    private List<ForumData> mForums;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.activity_board, container, false);

        // Get the unlocks
        mLocale = mSharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");

        // Let's get that data
        mForums = new ArrayList<ForumData>();

        // Init the views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Setup the TextView
        mTextTitle = (TextView) v.findViewById(R.id.text_board_title);
        v.findViewById(R.id.wrap_top).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View sv) {

                generateDialogLanguageList(mContext, DataBank.getLanguages(),
                        DataBank.getLocales()).show();

            }
        });

        // Setup the ListVIew
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(new ForumListAdapter(mContext, mForums, mLayoutInflater));

    }

    @Override
    public void onResume() {

        super.onResume();

        if (mForums == null || mForums.isEmpty()) {
            reload();
        }

    }

    public void reload() {

        // Is forums null?
        if (mForums == null) {

            new AsyncGetForums(mContext).execute();

        } else {

            new AsyncGetForums(null).execute();

        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        // Always called from this one
        ForumActivity parent = (ForumActivity) getActivity();

        // Let's open the forum
        parent.openForum(

                new Intent().putExtra(

                        "forumId", id

                ).putExtra(

                        "forumTitle", ((ForumData) v.getTag()).getTitle()

                )

        );

    }

    private class AsyncGetForums extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;
        private ProgressDialog progressDialog;
        private String title;

        // Construct
        public AsyncGetForums(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {

            if (context != null) {

                progressDialog = new ProgressDialog(this.context);
                progressDialog.setTitle(R.string.general_wait);
                progressDialog.setMessage("Downloading the forums...");
                progressDialog.show();

            }

        }

        @SuppressWarnings("unchecked")
        // I know what I'm doing... :D
        @Override
        protected Boolean doInBackground(Void... arg0) {

            try {

                Object[] result = new ForumClient().getForums(mLocale);
                title = (String) result[0];
                mForums = (List<ForumData>) result[1];
                return (mForums != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context != null && this.progressDialog != null) {

                this.progressDialog.dismiss();

            }

            // update the title
            mTextTitle.setText(title);

            if (mListView.getAdapter() != null) {

                ((ForumListAdapter) mListView.getAdapter()).setItemArray(mForums);

            }

        }

    }

    public Dialog generateDialogLanguageList(final Context context,
                                             final String[] languages, final String[] locales) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and the view
        builder.setTitle(R.string.info_forum_lang);

        builder.setSingleChoiceItems(

                languages, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mSharedPreferences.edit()
                        .putString(Constants.SP_BL_FORUM_LOCALE, locales[item])
                        .commit();
                mLocale = locales[item];
                reload();
                dialog.dismiss();

            }

        }

        );

        // CREATE
        return builder.create();

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }
}
