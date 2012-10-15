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

package com.ninetwozero.battlelog.activity.profile.weapon;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.WeaponListAdapter;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.WeaponDataWrapper;

public class WeaponListFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;

    // Elements
    private ListView mListView;

    // Misc
    private List<WeaponDataWrapper> mItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_items,
                container, false);

        // Get the unlocks
        mItems = ((WeaponListActivity) mContext).getItemsForFragment(mViewPagerPosition);

        // Init views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Setup the ListView
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(new WeaponListAdapter(mItems, mLayoutInflater));

    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public int getViewPagerPosition() {

        return mViewPagerPosition;

    }

    public void setViewPagerPosition(int p) {

        mViewPagerPosition = p;

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        ((WeaponListActivity) mContext).open((WeaponDataWrapper) v.getTag());

    }

    public void showWeapons(List<WeaponDataWrapper> data) {

        // Let's set the data
        ((WeaponListAdapter) mListView.getAdapter()).setData(data);

    }

    @Override
    public void reload() {

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }
}
