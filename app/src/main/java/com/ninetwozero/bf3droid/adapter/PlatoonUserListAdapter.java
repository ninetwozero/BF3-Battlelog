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
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonMember;

import java.util.List;

public class PlatoonUserListAdapter extends BaseAdapter {

    private List<PlatoonMember> platoonMembers;
    private LayoutInflater layoutInflater;
    private TextView textUser;

    public PlatoonUserListAdapter(List<PlatoonMember> p, LayoutInflater l) {
        platoonMembers = p;
        layoutInflater = l;
    }

    @Override
    public int getCount() {
        return (platoonMembers != null) ? platoonMembers.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (platoonMembers.get(position).getId() == 0) {
            return 0;
        } else if (platoonMembers.get(position).getMembershipLevel() == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public PlatoonMember getItem(int position) {
        return this.platoonMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.platoonMembers.get(position).getId();
    }

    public long getPersonaId(int position) {
        return this.platoonMembers.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlatoonMember platoonMember = getItem(position);

        if (getItemViewType(position) == 0) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_friends_divider, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.text_title)).setText(platoonMember.getUser().getUserName());
            convertView.setOnClickListener(null);
            convertView.setOnLongClickListener(null);
        } else if (getItemViewType(position) == 1) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item_request, parent, false);
            }

            textUser = (TextView) convertView.findViewById(R.id.text_user);
            textUser.setText(platoonMember.getUser().getUserName());
            convertView.setTag(platoonMember);
        } else {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_item_user, parent, false);

            }

            textUser = (TextView) convertView.findViewById(R.id.text_user);
            textUser.setText(platoonMember.getUser().getUserName());
            convertView.setTag(platoonMember);
        }
        return convertView;
    }

    public void setPlatoonMembers(List<PlatoonMember> pa) {
        platoonMembers = pa;
        notifyDataSetChanged();
    }
}
