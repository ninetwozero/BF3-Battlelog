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

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class NewsListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<NewsData> itemArray;
    private LayoutInflater layoutInflater;

    // Construct
    public NewsListAdapter(Context c, List<NewsData> fi, LayoutInflater l) {

        context = c;
        itemArray = fi;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (itemArray != null) ? itemArray.size() : 0;

    }

    @Override
    public NewsData getItem(int position) {

        return this.itemArray.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.itemArray.get(position).getId();

    }

    public void setItemArray(List<NewsData> ia) {

        this.itemArray = ia;
        this.notifyDataSetInvalidated();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        NewsData currentItem = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_news,
                    parent, false);

        }

        // Parse the data
        String authorText = context.getString(R.string.info_news_posted_by)
                .replace("{author}", currentItem.getAuthor().getUsername())
                .replace("{date}", PublicUtils.getRelativeDate(context, currentItem.getDate()));

        // Parse it!
        String commentText = ((currentItem.getNumComments() == 1) ? context
                .getString(R.string.info_comment_s) : context
                .getString(R.string.info_comment_p)
                ).replace("{num}", currentItem.getNumComments() + "");

        // Set the views
        ((TextView) convertView.findViewById(R.id.text_title)).setText(currentItem.getTitle());
        ((TextView) convertView.findViewById(R.id.text_author)).setText(Html.fromHtml(authorText));
        ((TextView) convertView.findViewById(R.id.text_comment)).setText(commentText);

        // Hook it up on the tag
        convertView.setTag(currentItem);

        // Send it back
        return convertView;
    }

}
