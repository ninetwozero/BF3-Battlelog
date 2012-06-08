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

package com.ninetwozero.battlelog.activity.forum;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.ThreadListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncCreateNewThread;
import com.ninetwozero.battlelog.datatypes.DefaultFragment;
import com.ninetwozero.battlelog.datatypes.ForumData;
import com.ninetwozero.battlelog.datatypes.ForumThreadData;
import com.ninetwozero.battlelog.handlers.ForumHandler;
import com.ninetwozero.battlelog.misc.BBCodeUtils;
import com.ninetwozero.battlelog.misc.Constants;

public class ForumFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private ForumData forumData;
    private ForumHandler forumHandler;

    // Elements
    private ListView listView;
    private ThreadListAdapter threadListAdapter;
    private TextView textTitle;
    private RelativeLayout wrapLoader;
    private Button buttonMore, buttonPost;
    private EditText textareaTitle, textareaContent;

    // Misc
    private long forumId;
    private long latestRefresh;
    private int currentPage;
    private boolean loadFresh;
    private String locale;
    private Intent storedRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.forum_view,
                container, false);

        // Get the unlocks
        locale = sharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");
        forumHandler = new ForumHandler();
        
        // Init the views
        initFragment(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Setup the ListView
        textTitle = (TextView) v.findViewById(R.id.text_title);
        listView = (ListView) v.findViewById(android.R.id.list);
        listView.setAdapter(threadListAdapter = new ThreadListAdapter(context, null, layoutInflater));
        getActivity().registerForContextMenu(listView);
        buttonPost = (Button) v.findViewById(R.id.button_new);
        buttonMore = (Button) v.findViewById(R.id.button_more);
        textareaTitle = (EditText) v.findViewById(R.id.textarea_title);
        textareaContent = (EditText) v.findViewById(R.id.textarea_content);

        // Set the click listeners
        buttonMore.setOnClickListener(

                new OnClickListener() {

                    @Override
                    public void onClick(View sv) {

                        // Validate
                        if ((currentPage - 1) == forumData.getNumPages()) {

                            sv.setVisibility(View.GONE);

                        }

                        // Do the "get more"-thing
                        new AsyncLoadMore(context, forumId).execute(++currentPage);

                    }
                }

                );

        // Let's set the onClick events
        buttonPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Let's get the content
                String title = textareaTitle.getText().toString();
                String content = textareaContent.getText().toString();

                // Let's see
                if (title.equals("")) {

                    Toast.makeText(context, "You need to enter a title for your thread.",
                            Toast.LENGTH_SHORT).show();
                    return;

                } else if (content.equals("")) {

                    Toast.makeText(context, "You need to enter some content for your thread.",
                            Toast.LENGTH_SHORT).show();
                    return;

                }

                // Parse for the BBCODE!
                content = BBCodeUtils.toBBCode(content, null);

                // Ready... set... go!
                new AsyncCreateNewThread(context, forumId).execute(title, content,
                        sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

            }

        });

        // Last but not least, the loader
        wrapLoader = (RelativeLayout) v.findViewById(R.id.wrap_loader);

        currentPage = 1;
        loadFresh = false;

        // Do we have one?
        if (storedRequest != null) {

            openForum(storedRequest);

        }

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();

    }

    public void reload() {

        // Do we have a forumId?
        if (forumId == 0) {
            return;
        }

        // Set it up
        long now = System.currentTimeMillis() / 1000;

        if (forumData == null || loadFresh) {

            new AsyncGetThreads(context, listView).execute(forumId);
            loadFresh = false;

        } else {

            if ((latestRefresh + 300) < now) {

                new AsyncGetThreads(null, listView).execute(forumId);

            } else {

                Log.d(Constants.DEBUG_TAG, "It's still fresh enough if you ask me!");

            }

        }

        // Save the latest refresh
        latestRefresh = now;

    }

    public void manualReload() {

        // Set it up
        latestRefresh = 0;
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
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1600);
                rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                wrapLoader.setVisibility(View.VISIBLE);
                wrapLoader.findViewById(R.id.image_loader).setAnimation(rotateAnimation);
                rotateAnimation.start();

            }

        }

        @Override
        protected Boolean doInBackground(Long... arg0) {

            try {

                forumData = new ForumHandler().getThreads(locale,
                        arg0[0]);
                return (forumData != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context != null) {

                if (forumData.getNumPages() > 1) {

                    buttonMore.setVisibility(View.VISIBLE);
                    buttonMore
                            .setText(R.string.info_xml_feed_button_pagination);

                } else {

                    buttonMore.setVisibility(View.GONE);

                }

                if (results) {

                    textTitle.setText(forumData.getTitle());
                    ((ThreadListAdapter) list.getAdapter()).set(forumData
                            .getThreads());

                    listView.post(

                            new Runnable() {

                                @Override
                                public void run() {

                                    // Set the selection
                                    listView.setSelection(0);

                                    // Hide it
                                    wrapLoader.setVisibility(View.GONE);
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

            buttonMore.setText(R.string.label_downloading);
            buttonMore.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Integer... arg0) {

            try {

                page = arg0[0];
                forumHandler.setForumId(forumId);
                threads = forumHandler.getThreads(locale, page);
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

                    threadListAdapter.add(threads);
                    buttonMore.setText(R.string.info_xml_feed_button_pagination);

                } else {

                    Toast.makeText(context,
                            R.string.info_xml_threads_more_false,
                            Toast.LENGTH_SHORT).show();

                }

                buttonMore.setEnabled(true);

            }

        }

    }

    public void openForum(Intent data) {

        if (textTitle == null) {

            storedRequest = data;

        } else {

            forumId = data.getLongExtra("forumId", 0);
            textTitle.setText(data.getStringExtra("forumTitle"));
            loadFresh = true;
            reload();

        }

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

}
