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
import com.ninetwozero.bf3droid.util.Contributors;

public class AboutCreditsFragment extends ListFragment implements DefaultFragment {
    private Context context;
    private LayoutInflater layoutInflater;
    private ListView listView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        final View view = layoutInflater.inflate(R.layout.tab_content_main_credits, container, false);

        initFragment(view);
        return view;
    }

    @Override
    public void initFragment(final View view) {
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(new CreditListAdapter(Contributors.getContributors(), layoutInflater));
    }

    @Override
    public void reload() {

    }

    @Override
    public void onListItemClick(final ListView lv, final View v, final int position, final long id) {
        final String url = String.valueOf(v.getTag());

        if ("".equals(url)) {
            Toast.makeText(context, R.string.info_credits_nolink, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
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
