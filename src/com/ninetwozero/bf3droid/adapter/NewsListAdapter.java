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

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.NewsData;
import com.ninetwozero.bf3droid.misc.PublicUtils;

import java.util.List;

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
                .replace("{author}", currentItem.getAuthorName())
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
