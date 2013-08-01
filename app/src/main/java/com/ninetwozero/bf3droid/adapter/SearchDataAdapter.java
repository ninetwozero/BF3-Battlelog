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

package com.ninetwozero.bf3droid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.GeneralSearchResult;
import com.ninetwozero.bf3droid.datatype.PlatoonData;
import com.ninetwozero.bf3droid.datatype.ProfileData;

import java.util.List;

public class SearchDataAdapter extends BaseAdapter {

    private List<GeneralSearchResult> resultList;
    private LayoutInflater layoutInflater;

    public SearchDataAdapter(List<GeneralSearchResult> m, LayoutInflater l) {
        resultList = m;
        layoutInflater = l;
    }

    @Override
    public int getCount() {
        return (resultList != null) ? resultList.size() : 0;
    }

    @Override
    public GeneralSearchResult getItem(int position) {
        return this.resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        GeneralSearchResult temp = this.resultList.get(position);
        return (temp.hasProfileData() ? temp.getProfileData().getId() : temp.getPlatoonData().getId());
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).hasProfileData()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        GeneralSearchResult currentItem = getItem(position);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_search_profile, parent, false);
        }

        if (getItemViewType(position) == 0) {
            ProfileData profileData = currentItem.getProfileData();
            ((TextView) view.findViewById(R.id.result_item_name)).setText(profileData.getUsername());
            ((TextView) view.findViewById(R.id.result_item_group)).setText(R.string.search_result_group_user);
        } else {
            PlatoonData platoonData = currentItem.getPlatoonData();
            ((TextView) view.findViewById(R.id.result_item_name)).setText("[" + platoonData.getTag() + "] " + platoonData.getName());
            ((TextView) view.findViewById(R.id.result_item_group)).setText(R.string.search_result_group_platoon);
        }
        view.setTag(currentItem);
        return view;
    }

    public void setResultList(List<GeneralSearchResult> array) {
        this.resultList = array;
        this.notifyDataSetInvalidated();
    }

    public void addItem(List<GeneralSearchResult> array) {
        this.resultList.addAll(array);
    }
}
