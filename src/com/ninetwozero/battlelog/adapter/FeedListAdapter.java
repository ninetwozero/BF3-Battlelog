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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.CommentData;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.misc.PublicUtils;

import java.util.List;

public class FeedListAdapter extends BaseAdapter {

	// Attributes
	private Context mContext;
	private List<FeedItem> mItems;
	private LayoutInflater mLayoutInflater;

	// Construct
	public FeedListAdapter(Context c, List<FeedItem> fi, LayoutInflater l) {
		mContext = c;
		mItems = fi;
		mLayoutInflater = l;

	}

	@Override
	public int getCount() {
		return (mItems != null) ? mItems.size() : 0;
	}

	@Override
	public FeedItem getItem(int position) {
		return this.mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.mItems.get(position).getId();
	}

	public void setItems(List<FeedItem> ia) {
		this.mItems = ia;
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Get the current item
		FeedItem currentItem = getItem(position);

		// Recycle
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_feed,
					parent, false);
		}

		// Grab a few views before we start
		TextView viewAllText = (TextView) convertView
				.findViewById(R.id.text_comments_overflow);

		// Set the views
		((TextView) convertView.findViewById(R.id.text_title)).setText(

		!currentItem.isCensored() ? Html.fromHtml(currentItem.getTitle())
				: mContext.getString(R.string.general_censored)

		);

		// How many likes/comments?
		String textHooah = (currentItem.getNumLikes() == 1) ? mContext
				.getString(R.string.info_hooah_s) : mContext
				.getString(R.string.info_hooah_p);
		String textComments = (currentItem.getNumComments() == 1) ? mContext
				.getString(R.string.info_comment_s) : mContext
				.getString(R.string.info_comment_p);
		String content = textComments.replace("{num}",
				currentItem.getNumComments() + "");

		// Set the ImageView
		ImageView imageAvatar = (ImageView) convertView
				.findViewById(R.id.image_avatar);
		imageAvatar.setImageBitmap(BitmapFactory.decodeFile(PublicUtils
				.getCachePath(mContext)
				+ currentItem.getAvatarForPost()
				+ ".png"));

		// Fill the actual content
		((TextView) convertView.findViewById(R.id.text_date))
				.setText(PublicUtils.getRelativeDate(mContext,
						currentItem.getDate()));
		((TextView) convertView.findViewById(R.id.text_hooah))
				.setText(textHooah.replace("{num}", currentItem.getNumLikes()
						+ ""));
		((TextView) convertView.findViewById(R.id.text_comment))
				.setText(content);

		// Do we need to populate comments?
		if (currentItem.hasPreloadedComments()) {
			List<CommentData> comments = currentItem.getPreloadedComments();
			int numPreloaded = comments.size(); // 1:2
			int[] commentWrappers = new int[] { R.id.wrap_comment2,
					R.id.wrap_comment1 };
			for (int i = 0, max = commentWrappers.length; i < max; i++) {

				// Do we have an item?
				if (numPreloaded == i) {
					convertView.findViewById(commentWrappers[i]).setVisibility(
							View.GONE);
					continue;
				}

				// Get
				View commentRoot = convertView.findViewById(commentWrappers[i]);
				CommentData comment = comments.get(i);
				ImageView commentImageView = (ImageView) commentRoot
						.findViewById(R.id.image_avatar);

				// Try to set the image first
				commentImageView.setImageBitmap(BitmapFactory
						.decodeFile(PublicUtils.getCachePath(mContext)
								+ comment.getGravatar() + ".png"));

				// Set the texts
				((TextView) commentRoot.findViewById(R.id.text_name))
						.setText(comment.getAuthor().getUsername());
				((TextView) commentRoot.findViewById(R.id.text_comment))
						.setText(comment.getContent());
				((TextView) commentRoot.findViewById(R.id.text_date))
						.setText(PublicUtils.getRelativeDate(mContext,
								comment.getTimestamp()));

				commentRoot.setVisibility(View.VISIBLE);
			}

			// We might have to show the "SinglePostView"-button!
			if (currentItem.getNumComments() > 2) {
				viewAllText.setVisibility(View.VISIBLE);
			} else {
				viewAllText.setVisibility(View.GONE);
			}
		} else {
			convertView.findViewById(R.id.wrap_comment1).setVisibility(
					View.GONE);
			convertView.findViewById(R.id.wrap_comment2).setVisibility(
					View.GONE);
			viewAllText.setVisibility(View.GONE);
		}

		// Hook it up on the tag
		convertView.setTag(currentItem);

		// Send it back
		return convertView;
	}

}
