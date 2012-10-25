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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.ChatMessage;
import com.ninetwozero.battlelog.misc.PublicUtils;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    // Attributes
    private Context context;
    private List<ChatMessage> messageArray;
    private LayoutInflater layoutInflater;
    private TextView textUsername, textMessage, textTimestamp;
    private String thisUser;

    // Construct
    public ChatListAdapter(Context c, List<ChatMessage> m, String tu,
                           LayoutInflater l) {

        context = c;
        messageArray = m;
        thisUser = tu;
        layoutInflater = l;
    }

    @Override
    public int getCount() {

        return (messageArray != null) ? messageArray.size() : 0;

    }

    @Override
    public ChatMessage getItem(int position) {
        return this.messageArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getSender().equals(thisUser)) {
            return 1;
        } else {
            return 0;

        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setMessages(List<ChatMessage> m) {
        messageArray = m;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current item
        ChatMessage currentMessage = getItem(position);

        // Recycle
        if (convertView == null) {

            // Let's get the view type
            if (getItemViewType(position) == 1) {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_chat_local, parent, false);

            } else {

                convertView = layoutInflater.inflate(
                        R.layout.list_item_chat_remote, parent, false);

            }
        }

        // Grab the fields
        textUsername = (TextView) convertView.findViewById(R.id.text_username);
        textTimestamp = (TextView) convertView.findViewById(R.id.text_timestamp);
        textMessage = (TextView) convertView.findViewById(R.id.text_message);

        // Set the TextViews
        textUsername.setText(currentMessage.getSender());
        textMessage.setText(Html.fromHtml(currentMessage.getMessage()));
        textTimestamp.setText(PublicUtils.getRelativeDate(context,
                currentMessage.getTimestamp()));

        // Store the object
        convertView.setTag(currentMessage);

        return convertView;
    }

}
