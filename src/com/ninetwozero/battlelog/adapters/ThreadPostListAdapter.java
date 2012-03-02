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

package com.ninetwozero.battlelog.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ThreadPostListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private ArrayList<Board.PostData> itemArray;
    private LayoutInflater layoutInflater;

    // Construct
    public ThreadPostListAdapter(Context c, ArrayList<Board.PostData> m,
            LayoutInflater l) {

        context = c;
        itemArray = m;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (itemArray != null) ? itemArray.size() : 0;

    }

    @Override
    public Board.PostData getItem(int position) {

        return this.itemArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.itemArray.get(position).getThreadId();

    }

    @Override
    public int getItemViewType(int position) {

        if (getItem(position).getThreadId() == 0) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public int getViewTypeCount() {

        return 2;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        Board.PostData currentItem = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_forum_post,
                    parent, false);

        }

        // Store the object
        if (currentItem.isOfficial()) {

            convertView.setBackgroundColor(context.getResources().getColor(
                    R.color.officialblue));

        } else {

            convertView.setBackgroundColor(context.getResources().getColor(
                    android.R.color.white));

        }

        TextView textView = (TextView) convertView
                .findViewById(R.id.text_content);
        textView.setText(

                !currentItem.isCensored() ? Html.fromHtml(currentItem.getContent())
                        : context.getString(R.string.general_censored)

                );

        ((TextView) convertView.findViewById(R.id.text_posted_by)).setText(

                Html.fromHtml(

                        context.getString(R.string.info_xml_threaddate)
                                .replace(

                                        "{date}",
                                        PublicUtils.getRelativeDate(context, currentItem.getDate())

                                ).replace(

                                        "{user}", currentItem.getProfileData().getAccountName()

                                )

                        )

                );

        // Lazy-keep
        convertView.setTag(currentItem);

        // R-TURN
        return convertView;

    }

    public void set(ArrayList<Board.PostData> array) {

        this.itemArray = array;
        this.notifyDataSetChanged();

    }

    public void add(ArrayList<Board.PostData> array) {

        this.itemArray.addAll(array);
        this.notifyDataSetChanged();

    }

}
