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

package com.ninetwozero.battlelog.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.SavedForumThreadData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class SavedThreadListAdapter extends BaseAdapter {

	// Attributes
	private Context context;
	private List<SavedForumThreadData> itemArray;
	private LayoutInflater layoutInflater;

	// Construct
	public SavedThreadListAdapter(Context c, List<SavedForumThreadData> m,
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
	public SavedForumThreadData getItem(int position) {

		return itemArray.get(position);

	}

	@Override
	public long getItemId(int position) {

		return itemArray.get(position).getId();

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
		SavedForumThreadData currentItem = getItem(position);

		// Recycle
		if (convertView == null) {

			convertView = layoutInflater.inflate(
					R.layout.list_item_thread_saved, parent, false);

		}

		((TextView) convertView.findViewById(R.id.text_title))
				.setText(currentItem.getTitle());
		((TextView) convertView.findViewById(R.id.text_thread_last_post))
				.setText(

				Html.fromHtml(

				context.getString(R.string.info_xml_threadreplydate)
						.replace(

								"{date}",
								PublicUtils.getRelativeDate(context,
										currentItem.getDateLastPost())

						).replace(

						"{user}", currentItem.getLastPoster().getUsername()

						)

				)

				);
		((TextView) convertView.findViewById(R.id.text_post_last_checked))
				.setText(

				Html.fromHtml(

				context.getString(R.string.info_forum_adapter_last_checked)
						.replace(

								"{date}",
								PublicUtils.getRelativeDate(context,
										currentItem.getDateLastChecked())

						)

				)

				);

		convertView.findViewById(R.id.bar_status).setBackgroundColor(
				context.getResources().getColor(
						currentItem.hasUnread() ? R.color.green
								: R.color.lightgrey));

		// Store the object
		convertView.setTag(currentItem);

		// R-TURN
		return convertView;

	}

	public void set(List<SavedForumThreadData> array) {

		itemArray = array;
		notifyDataSetInvalidated();

	}

	public void add(List<SavedForumThreadData> array) {

		itemArray.addAll(array); /* TODO FIX THIS */
		notifyDataSetChanged();

	}

}
