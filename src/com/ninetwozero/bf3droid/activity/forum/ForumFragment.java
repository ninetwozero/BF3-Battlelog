/*
    context file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.forum;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.adapter.ThreadListAdapter;
import com.ninetwozero.bf3droid.asynctask.AsyncCreateNewThread;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.ForumData;
import com.ninetwozero.bf3droid.datatype.ForumThreadData;
import com.ninetwozero.bf3droid.http.ForumClient;
import com.ninetwozero.bf3droid.misc.BBCodeUtils;
import com.ninetwozero.bf3droid.misc.Constants;

public class ForumFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;
    private ForumData mForumData;
    private ForumClient mForumHandler;

    // Elements
    private ListView mListView;
    private ThreadListAdapter mListAdapter;
    private TextView mTextTitle;
    private RelativeLayout mWrapLoader;
    private Button mButtonMore;
    private Button mButtonPost;
    private EditText mTextareaTitle;
    private EditText mTextareaContent;

    // Misc
    private long mForumId;
    private long mLatestRefresh;
    private int mCurrentPage;
    private boolean mLoadFresh;
    private String mLocale;
    private Intent mStoredRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.activity_forum, container, false);

        // Get the unlocks
        mLocale = mSharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");
        mForumHandler = new ForumClient();

        // Init the views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Setup the ListView
        mTextTitle = (TextView) v.findViewById(R.id.text_title);
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(mListAdapter = new ThreadListAdapter(mContext,
                null, mLayoutInflater));
        getActivity().registerForContextMenu(mListView);
        mButtonPost = (Button) v.findViewById(R.id.button_new);
        mButtonMore = (Button) v.findViewById(R.id.button_more);
        mTextareaTitle = (EditText) v.findViewById(R.id.textarea_title);
        mTextareaContent = (EditText) v.findViewById(R.id.textarea_content);

        // Set the click listeners
        mButtonMore.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View sv) {

                        // Validate
                        if ((mCurrentPage - 1) == mForumData.getNumPages()) {

                            sv.setVisibility(View.GONE);

                        }

                        // Do the "get more"-thing
                        new AsyncLoadMore(mContext, mForumId).execute(++mCurrentPage);

                    }
                }

        );

        // Let's set the onClick events
        mButtonPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Let's get the content
                String title = mTextareaTitle.getText().toString();
                String content = mTextareaContent.getText().toString();

                // Let's see
                if ("".equals(title)) {

                    Toast.makeText(mContext,
                            "You need to enter a title for your thread.",
                            Toast.LENGTH_SHORT).show();

                } else if ("".equals(content)) {

                    Toast.makeText(mContext,
                            "You need to enter some content for your thread.",
                            Toast.LENGTH_SHORT).show();

                }

                // Parse for the BBCODE!
                content = BBCodeUtils.toBBCode(content, null);

                // Ready... set... go!
                new AsyncCreateNewThread(mContext, mForumId).execute(title,
                        content, mSharedPreferences.getString(
                        Constants.SP_BL_PROFILE_CHECKSUM, ""));

            }

        });

        // Last but not least, the loader
        mWrapLoader = (RelativeLayout) v.findViewById(R.id.wrap_loader);

        mCurrentPage = 1;
        mLoadFresh = false;

        // Do we have one?
        if (mStoredRequest != null) {

            openForum(mStoredRequest);

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();

    }

    public void reload() {

        // Do we have a forumId?
        if (mForumId > 0) {

            // Set it up
            long now = System.currentTimeMillis() / 1000;

            if (mForumData == null || mLoadFresh) {

                new AsyncGetThreads(mContext, mListView).execute(mForumId);
                mLoadFresh = false;

            } else {

                if ((mLatestRefresh + 300) < now) {

                    new AsyncGetThreads(null, mListView).execute(mForumId);

                }

            }

            // Save the latest refresh
            mLatestRefresh = now;

        }

    }

    public void manualReload() {

        // Set it up
        mLatestRefresh = 0;
        reload();

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        // Always called from context one
        ForumActivity parent = (ForumActivity) getActivity();

        // Let's open the forum
        parent.openThread(

                new Intent().putExtra(

                        "threadId", id

                ).putExtra(

                        "threadTitle", ((ForumThreadData) v.getTag()).getTitle()

                )

        );

    }

    private class AsyncGetThreads extends AsyncTask<Long, Void, Boolean> {

        // Attributes
        private Context context;
        private ListView list;
        private RotateAnimation rotateAnimation;

        // Construct
        public AsyncGetThreads(Context c, ListView l) {

            context = c;
            list = l;

        }

        @Override
        protected void onPreExecute() {

            if (context != null) {

                rotateAnimation = new RotateAnimation(0, 359,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1600);
                rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                mWrapLoader.setVisibility(View.VISIBLE);
                mWrapLoader.findViewById(R.id.image_loader).setAnimation(
                        rotateAnimation);
                rotateAnimation.start();

            }

        }

        @Override
        protected Boolean doInBackground(Long... arg0) {

            try {

                mForumData = new ForumClient().getThreads(mLocale, arg0[0]);
                return (mForumData != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context != null) {

                if (mForumData.getNumPages() > 1) {

                    mButtonMore.setVisibility(View.VISIBLE);
                    mButtonMore
                            .setText(R.string.info_xml_feed_button_pagination);

                } else {

                    mButtonMore.setVisibility(View.GONE);

                }

                if (results) {

                    mTextTitle.setText(mForumData.getTitle());
                    ((ThreadListAdapter) list.getAdapter()).set(mForumData
                            .getThreads());

                    mListView.post(

                            new Runnable() {

                                @Override
                                public void run() {

                                    // Set the selection
                                    mListView.setSelection(0);

                                    // Hide it
                                    mWrapLoader.setVisibility(View.GONE);
                                    rotateAnimation.reset();

                                }

                            }

                    );

                }

            }

        }

    }

    private class AsyncLoadMore extends AsyncTask<Integer, Void, Boolean> {

        // Attributes
        private Context context;
        private long forumId;
        private int page;
        private List<ForumThreadData> threads;

        // Constructs
        public AsyncLoadMore(Context c, long f) {

            this.context = c;
            this.forumId = f;

        }

        @Override
        protected void onPreExecute() {

            mButtonMore.setText(R.string.label_downloading);
            mButtonMore.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Integer... arg0) {

            try {

                page = arg0[0];
                mForumHandler.setForumId(forumId);
                threads = mForumHandler.getThreads(mLocale, page);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context instanceof ForumActivity) {

                if (results) {

                    mListAdapter.add(threads);
                    mButtonMore
                            .setText(R.string.info_xml_feed_button_pagination);

                } else {

                    Toast.makeText(context,
                            R.string.info_xml_threads_more_false,
                            Toast.LENGTH_SHORT).show();

                }

                mButtonMore.setEnabled(true);

            }

        }

    }

    public void openForum(Intent data) {

        if (mTextTitle == null) {

            mStoredRequest = data;

        } else {

            mForumId = data.getLongExtra("forumId", 0);
            mTextTitle.setText(data.getStringExtra("forumTitle"));
            mLoadFresh = true;
            reload();

        }

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        return false;
    }

}
