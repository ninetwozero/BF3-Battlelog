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
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ThreadListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<Board.ThreadData> itemArray;
    private LayoutInflater layoutInflater;

    // Construct
    public ThreadListAdapter(Context c, List<Board.ThreadData> m,
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
    public Board.ThreadData getItem(int position) {

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
        Board.ThreadData currentItem = getItem(position);

        // Recycle
        if (getItemViewType(position) == 1) {

            if (convertView == null) {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_thread_separator, parent, false);

            }

            ((TextView) convertView.findViewById(R.id.text_title))
                    .setText(currentItem.getTitle());

        } else {

            if (convertView == null) {

                convertView = layoutInflater.inflate(R.layout.list_item_thread,
                        parent, false);

            }

            // Let's do the coloring
            if (currentItem.hasOfficialResponse()) {

                // Set the colors
                convertView.findViewById(R.id.bar_official).setBackgroundColor(
                        context.getResources().getColor(R.color.blue));

            } else {

                // Set the colors
                convertView.findViewById(R.id.bar_official).setBackgroundColor(
                        context.getResources().getColor(R.color.lightgrey));

            }

            // Set the title
            TextView textTitle = (TextView) convertView
                    .findViewById(R.id.string_title);
            textTitle.setText(currentItem.getTitle());
            if (!currentItem.isLocked()) {

                textTitle.setTextColor(context.getResources().getColor(
                        R.color.blue));

            } else {

                textTitle.setTextColor(context.getResources().getColor(
                        android.R.color.black));

            }

            // Set the rest of the TextViews
            ((TextView) convertView.findViewById(R.id.string_owner)).setText(

                    Html.fromHtml(

                            context.getString(R.string.info_xml_threaddate)
                                    .replace(

                                            "{date}",
                                            PublicUtils.getRelativeDate(context,
                                                    currentItem.getDate())

                                    ).replace(

                                            "{user}", currentItem.getOwner().getAccountName()

                                    )

                            )

                    );

            ((TextView) convertView.findViewById(R.id.string_post_last))
                    .setText(

                    Html.fromHtml(

                            context.getString(R.string.info_xml_threadreplydate)
                                    .replace(

                                            "{date}",
                                            PublicUtils.getRelativeDate(context,
                                                    currentItem.getLastPostDate())

                                    )
                                    .replace(

                                            "{user}",
                                            currentItem.getLastPoster()
                                                    .getAccountName()

                                    )

                            )

                    );

            ((TextView) convertView.findViewById(R.id.string_info)).setText(

                    Html.fromHtml(

                            context.getString(R.string.info_xml_threadinfo)
                                    .replace(

                                            "{num_posts}", currentItem.getNumPosts() + ""

                                    )
                                    .replace(

                                            "{num_official}",
                                            currentItem.getNumOfficialPosts() + ""

                                    )

                            )

                    );

        }

        // Store the object
        convertView.setTag(currentItem);

        // R-TURN
        return convertView;

    }

    public void set(List<Board.ThreadData> array) {

        this.itemArray = array;
        this.notifyDataSetInvalidated();

    }

    public void add(List<Board.ThreadData> array) {

        this.itemArray.addAll(array); /* TODO FIX THIS */
        this.notifyDataSetChanged();

    }

}
