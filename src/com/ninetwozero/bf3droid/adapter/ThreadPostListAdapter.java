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
import com.ninetwozero.bf3droid.datatype.ForumPostData;
import com.ninetwozero.bf3droid.misc.PublicUtils;

public class ThreadPostListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<ForumPostData> items;
    private LayoutInflater layoutInflater;

    // Construct
    public ThreadPostListAdapter(Context c, List<ForumPostData> m,
                                 LayoutInflater l) {

        context = c;
        items = m;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (items != null) ? items.size() : 0;

    }

    @Override
    public ForumPostData getItem(int position) {

        return this.items.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.items.get(position).getThreadId();

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
        ForumPostData currentItem = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_forum_post,
                    parent, false);

        }

        // Text
        TextView textPostedBy = (TextView) convertView.findViewById(R.id.text_posted_by);
        TextView textView = (TextView) convertView.findViewById(R.id.text_content);

        // Store the object
        if (currentItem.isOfficial()) {

            convertView.setBackgroundColor(context.getResources().getColor(R.color.officialblue));
            textPostedBy.setBackgroundColor(0xFFEEEEEE);

        } else {

            convertView.setBackgroundColor(0xFFFFFFFF);
            textPostedBy.setBackgroundColor(0xFFEEEEEE);

        }

        textView.setText(

                !currentItem.isCensored() ? Html.fromHtml(currentItem.getContent())
                        : context.getString(R.string.general_censored)

        );

        textPostedBy.setText(

                Html.fromHtml(

                        context.getString(R.string.info_xml_threaddate)
                                .replace(

                                        "{date}",
                                        PublicUtils.getRelativeDate(context, currentItem.getDate())

                                ).replace(

                                "{user}", currentItem.getProfileData().getUsername()

                        )

                )

        );

        // Lazy-keep
        convertView.setTag(currentItem);

        // R-TURN
        return convertView;

    }

    public void set(List<ForumPostData> array) {

        this.items = array;
        this.notifyDataSetChanged();

    }

    public void add(List<ForumPostData> array) {

        this.items.addAll(array);
        this.notifyDataSetChanged();

    }

}
