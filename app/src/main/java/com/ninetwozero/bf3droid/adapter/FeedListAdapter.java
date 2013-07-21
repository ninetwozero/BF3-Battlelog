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
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.CommentData;
import com.ninetwozero.bf3droid.datatype.FeedItem;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.util.ImageLoader;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;

import java.util.List;

public class FeedListAdapter extends BaseAdapter {
    private Context context;
    private List<FeedItem> feedItems;
    private LayoutInflater layoutInflater;
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
    private static final String BATTLELOG_SUFFIX = "?s=100&d=http%3A%2F%2Fbattlelog-cdn.battlefield.com%2Fcdnprefix%2Favatar1%2Fpublic%2Fbase%2Fshared%2Fdefault-avatar-100.png";


    public FeedListAdapter(Context c, List<FeedItem> fi, LayoutInflater l) {
        context = c;
        feedItems = fi;
        layoutInflater = l;
    }

    @Override
    public int getCount() {
        return (feedItems != null) ? feedItems.size() : 0;
    }

    @Override
    public FeedItem getItem(int position) {
        return this.feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.feedItems.get(position).getId();
    }

    public void setItems(List<FeedItem> ia) {
        this.feedItems = ia;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem currentItem = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_feed, parent, false);
        }

        TextView viewAllText = (TextView) convertView.findViewById(R.id.text_comments_overflow);

        ((TextView) convertView.findViewById(R.id.text_title)).setText(
                !currentItem.isCensored() ? Html.fromHtml(currentItem.getTitle()): context.getString(R.string.general_censored)
        );

        String textHooah = (currentItem.getNumLikes() == 1) ? context
                .getString(R.string.info_hooah_s) : context
                .getString(R.string.info_hooah_p);
        String textComments = (currentItem.getNumComments() == 1) ? context
                .getString(R.string.info_comment_s) : context
                .getString(R.string.info_comment_p);
        String content = textComments.replace("{num}",
                currentItem.getNumComments() + "");

        ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.image_avatar);
        provideImageLoader().loadImage(imageAvatar, imagePath(currentItem.getAvatarForPost()));

        ((TextView) convertView.findViewById(R.id.text_date))
                .setText(PublicUtils.getRelativeDate(context, currentItem.getDate()));
        ((TextView) convertView.findViewById(R.id.text_hooah))
                .setText(textHooah.replace("{num}", currentItem.getNumLikes() + ""));
        ((TextView) convertView.findViewById(R.id.text_comment)).setText(content);

        if (currentItem.hasPreloadedComments()) {
            List<CommentData> comments = currentItem.getPreloadedComments();
            int numPreloaded = comments.size(); // 1:2
            int[] commentWrappers = new int[]{R.id.wrap_comment2, R.id.wrap_comment1};
            for (int i = 0, max = commentWrappers.length; i < max; i++) {

                if (numPreloaded == i) {
                    convertView.findViewById(commentWrappers[i]).setVisibility(View.GONE);
                    continue;
                }

                View commentRoot = convertView.findViewById(commentWrappers[i]);
                CommentData comment = comments.get(i);
                ImageView commentImageView = (ImageView) commentRoot.findViewById(R.id.image_avatar);
                // Try to set the image first
                commentImageView.setImageBitmap(BitmapFactory
                        .decodeFile(PublicUtils.getCachePath(context)+ comment.getGravatar() + ".png"));

                // Set the texts
                ((TextView) commentRoot.findViewById(R.id.text_name)).setText(comment.getAuthor().getUsername());
                ((TextView) commentRoot.findViewById(R.id.text_comment)).setText(comment.getContent());
                ((TextView) commentRoot.findViewById(R.id.text_date)).setText(PublicUtils.getRelativeDate(context, comment.getTimestamp()));
                commentRoot.setVisibility(View.VISIBLE);
            }

            if (currentItem.getNumComments() > 2) {
                viewAllText.setVisibility(View.VISIBLE);
            } else {
                viewAllText.setVisibility(View.GONE);
            }
        } else {
            convertView.findViewById(R.id.wrap_comment1).setVisibility(View.GONE);
            convertView.findViewById(R.id.wrap_comment2).setVisibility(View.GONE);
            viewAllText.setVisibility(View.GONE);
        }

        convertView.setTag(currentItem);

        return convertView;
    }

    private String imagePath(String gravatarId) {
        return new StringBuilder(GRAVATAR_URL).append(gravatarId).append(BATTLELOG_SUFFIX).toString();
    }

    private ImageLoader provideImageLoader() {
        Context appContext = context;
        LoaderSettings settings = new LoaderSettings.SettingsBuilder()
                .withDisconnectOnEveryCall(true)
                .withCacheManager(new LruBitmapCache(appContext))
                .build(appContext);
        return new ImageLoader(new ImageManager(appContext, settings));
    }
}
