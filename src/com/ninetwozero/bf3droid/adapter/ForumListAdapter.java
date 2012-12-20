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

package com.ninetwozero.bf3droid.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.ForumData;

public class ForumListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<ForumData> itemArray;
    private LayoutInflater layoutInflater;

    // Construct
    public ForumListAdapter(Context c, List<ForumData> m,
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
    public ForumData getItem(int position) {

        return this.itemArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.itemArray.get(position).getForumId();

    }

    @Override
    public int getItemViewType(int position) {

        return 0;

    }

    @Override
    public int getViewTypeCount() {

        return 1;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        ForumData currentItem = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_forum,
                    parent, false);

        }

        // Set the TextViews
        ((TextView) convertView.findViewById(R.id.string_title))
                .setText(currentItem.getTitle());
        ((TextView) convertView.findViewById(R.id.string_desc))
                .setText(currentItem.getDescription());
        ((TextView) convertView.findViewById(R.id.string_info)).setText(

                Html.fromHtml(

                        context.getString(R.string.info_xml_forum_postsinthreads).replace(

                                "{num_posts}", currentItem.getNumPosts() + ""

                        ).replace(

                                "{num_threads}", currentItem.getNumThreads() + ""

                        )

                )

        );

        // Store the object
        convertView.setTag(currentItem);

        return convertView;

    }

    public void setItemArray(List<ForumData> array) {

        this.itemArray = array;
        this.notifyDataSetInvalidated();

    }

}
