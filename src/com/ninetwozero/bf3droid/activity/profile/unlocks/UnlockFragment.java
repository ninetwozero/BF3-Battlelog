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

package com.ninetwozero.bf3droid.activity.profile.unlocks;

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

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.UnlockListAdapter;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.UnlockData;

public class UnlockFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mViewPagerPosition;

    private ListView mListView;
    private List<UnlockData> mUnlocks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mLayoutInflater = inflater;
        
        if (mContext instanceof UnlockActivity) {
            mUnlocks = ((UnlockActivity) mContext).getItemsForFragment(mViewPagerPosition);
        }
        
        View view = mLayoutInflater.inflate(R.layout.tab_content_unlocks, container, false);
        initFragment(view);
        return view;
    }

    public void initFragment(View v) {
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(new UnlockListAdapter(mContext, mUnlocks, mLayoutInflater));
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
        // TODO: OPEN WEAPON STATISTICS
    }

    public void showUnlocks(List<UnlockData> unlockData) {
        ((UnlockListAdapter) mListView.getAdapter()).setData(unlockData);
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
