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

package com.ninetwozero.bf3droid.activity.aboutapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.ListView;
import android.widget.Toast;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.CreditListAdapter;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.misc.DataBank;

public class AboutCreditsFragment extends ListFragment implements
        DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Elements
    private ListView mListView;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        final View view = mLayoutInflater.inflate(
                R.layout.tab_content_main_credits, container, false);

        // Let's try this
        initFragment(view);

        // Return
        return view;

    }

    @Override
    public void initFragment(final View view) {

        // Get the listview
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new CreditListAdapter(DataBank.getContributors(),
                mLayoutInflater));

    }

    @Override
    public void reload() {

    }

    @Override
    public void onListItemClick(final ListView lv, final View v,
                                final int position, final long id) {

        // Get the url
        final String url = String.valueOf(v.getTag());

        // Is it empty?
        if ("".equals(url)) {
            Toast.makeText(mContext, R.string.info_credits_nolink,
                    Toast.LENGTH_SHORT).show();

        } else {
            // Let's send it somewhere
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(url)));

        }

    }

    @Override
    public Menu prepareOptionsMenu(final Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(final MenuItem item) {
        return false;
    }

}
