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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.CommentData;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class CommentListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<CommentData> comments;
    private LayoutInflater layoutInflater;
    private TextView textUsername, textMessage, textTimestamp;
    private ImageView imageAvatar;
    private String thisUser;

    // Construct
    public CommentListAdapter(Context c, List<CommentData> cd,
            LayoutInflater l) {

        context = c;
        comments = cd;
        layoutInflater = l;

    }

    @Override
    public int getCount() {

        return (comments != null) ? comments.size() : 0;

    }

    @Override
    public CommentData getItem(int position) {

        return this.comments.get(position);

    }

    @Override
    public long getItemId(int position) {

        return this.comments.get(position).getId();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        CommentData currentData = getItem(position);

        // Recycle
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_comment,
                    parent, false);

        }

        // Grab the fields
        textUsername = (TextView) convertView.findViewById(R.id.text_username);
        textTimestamp = (TextView) convertView
                .findViewById(R.id.text_timestamp);
        textMessage = (TextView) convertView.findViewById(R.id.text_message);
        imageAvatar = (ImageView) convertView.findViewById(R.id.image_avatar);

        // Set the TextViews
        textUsername.setText(currentData.getAuthor().getUsername());
        textMessage.setText(Html.fromHtml(currentData.getContent().replace("<",
                "&lt;")));
        textTimestamp.setText(PublicUtils.getRelativeDate(context,
                currentData.getTimestamp()));

        // Set the gravatar
        Bitmap b = BitmapFactory.decodeFile(

                PublicUtils.getCachePath(context)
                        + currentData.getAuthor().getGravatarHash() + ".png"

                );

        if (b == null) {

            imageAvatar.setImageResource(R.drawable.default_avatar);

        } else {

            imageAvatar.setImageBitmap(b);

        }

        imageAvatar.setTag(currentData.getAuthor());

        // Store the object
        convertView.setTag(currentData);

        return convertView;
    }

    public void setItemArray(List<CommentData> c) {

        comments = c;
        notifyDataSetChanged();

    }

}
