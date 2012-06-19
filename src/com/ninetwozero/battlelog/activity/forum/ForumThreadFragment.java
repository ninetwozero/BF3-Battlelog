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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.activity.profile.soldier.ProfileActivity;
import com.ninetwozero.battlelog.adapter.ThreadPostListAdapter;
import com.ninetwozero.battlelog.asynctask.AsyncPostInThread;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.ForumPostData;
import com.ninetwozero.battlelog.datatype.ForumThreadData;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.http.ForumClient;
import com.ninetwozero.battlelog.http.ProfileClient;
import com.ninetwozero.battlelog.misc.BBCodeUtils;
import com.ninetwozero.battlelog.misc.CacheHandler;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;

public class ForumThreadFragment extends ListFragment implements DefaultFragment {

    // Attributes
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private SharedPreferences mSharedPreferences;
    private ForumThreadData mThreadData;
    private ForumClient mForumHandler = new ForumClient();

    // Elements
    private ListView mListView;
    private ThreadPostListAdapter mListAdapter;
    private TextView mTextTitle;
    private RelativeLayout mWrapButtons;
    private RelativeLayout mWrapLoader;
    private Button mButtonPost;
    private Button mButtonPrev;
    private Button mButtonNext;
    private Button mButtonJump;
    private SlidingDrawer mSlidingDrawer;
    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private EditText mTextareaContent;

    // Misc
    private long mThreadId;
    private String mLocale;
    private int mCurrentPage;
    private Map<Long, String> mSelectedQuotes;
    private Integer[] mPageArray;
    private boolean mCaching;
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
        View view = mLayoutInflater.inflate(R.layout.forum_thread_view,
                container, false);

        // Get the locale
        mLocale = mSharedPreferences.getString(Constants.SP_BL_FORUM_LOCALE, "en");

        // Init the Fragment
        initFragment(view);
        setupBottom(view);

        // Return the view
        return view;

    }

    public void initFragment(View v) {

        // Are we to cache?
        mCaching = mSharedPreferences.getBoolean(Constants.SP_BL_FORUM_CACHE, true);

        // Setup the text
        mTextTitle = (TextView) v.findViewById(R.id.text_title_thread);

        // Setup the ListView
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setAdapter(mListAdapter = new ThreadPostListAdapter(mContext, null,
                mLayoutInflater));
        mListView.setDrawSelectorOnTop(false);
        getActivity().registerForContextMenu(mListView);

        // Let's get the button
        mButtonJump = (Button) v.findViewById(R.id.button_jump);
        mButtonPrev = (Button) v.findViewById(R.id.button_prev);
        mButtonNext = (Button) v.findViewById(R.id.button_next);
        mButtonPost = (Button) v.findViewById(R.id.button_new);
        mWrapButtons = (RelativeLayout) v.findViewById(R.id.wrap_buttons);

        // Last but not least, the loader
        mWrapLoader = (RelativeLayout) v.findViewById(R.id.wrap_loader);

        // Init the quotes and page
        if (mSelectedQuotes == null) {
            mSelectedQuotes = new HashMap<Long, String>();
        }

        // Do we have one?
        if (mStoredRequest != null) {

            openThread(mStoredRequest);

        }

        // Let's set the onClick events
        mButtonNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Increment the page #
                mCurrentPage++;

                // Let's do this
                new AsyncLoadPage(mContext, mThreadId).execute(mCurrentPage);

            }

        });

        // Let's set the onClick events
        mButtonPrev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // "Decrement"?
                mCurrentPage--;

                // Let's do this
                new AsyncLoadPage(mContext, mThreadId).execute(mCurrentPage);

            }

        });

        // Let's set the onClick events
        mButtonJump.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Generate a dialog
                generateDialogPage(mContext).show();

            }

        });

        // Let's set the onClick events
        mButtonPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // Generate a dialog
                String content = mTextareaContent.getText().toString();

                // Validate
                if ("".equals(content)) {

                    Toast.makeText(mContext, "You need to enter some content for your reply.",
                            Toast.LENGTH_SHORT).show();

                }

                // Parse for the BBCODE!
                content = BBCodeUtils.toBBCode(content, mSelectedQuotes);

                // Ready... set... go!
                new AsyncPostInThread(mContext, mThreadData, false).execute(content,
                        mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

            }

        });

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();

    }

    public void reload() {

        // Do we have a threadId?
        if (mThreadId == 0) {
            return;
        }

        // Set it up
        if (mThreadData == null || mCurrentPage == 1) {

            new AsyncGetPosts(mContext, mThreadId).execute(mCurrentPage);

        } else {

            new AsyncLoadPage(mContext, mThreadId).execute(mCurrentPage);

        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        getActivity().openContextMenu(v);

    }

    public void openThread(Intent data) {

        if (mTextTitle == null) {

            mStoredRequest = data;

        } else {

            mThreadId = data.getLongExtra("threadId", 0);
            mTextTitle.setText(data.getStringExtra("threadTitle"));
            mCurrentPage = data.getIntExtra("pageId", 1);
            reload();

        }

    }

    private class AsyncGetPosts extends AsyncTask<Integer, Void, Boolean> {

        // Attributes
        private Context context;
        private long tempThreadId;
        private int tempPageId;
        private List<ForumPostData> posts;
        private RotateAnimation rotateAnimation;

        // Construct
        public AsyncGetPosts(Context c, long t) {

            context = c;
            tempThreadId = t;

        }

        @Override
        protected void onPreExecute() {

            if (context != null) {

                rotateAnimation = new RotateAnimation(0, 359,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1600);
                rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                mWrapLoader.setVisibility(View.VISIBLE);
                mWrapLoader.findViewById(R.id.image_loader).setAnimation(rotateAnimation);
                rotateAnimation.start();

            }

        }

        @Override
        protected Boolean doInBackground(Integer... arg0) {

            try {

                // Store the param
                tempPageId = arg0[0];

                // Get the threadData
                mForumHandler.setThreadId(tempThreadId);
                mThreadData = mForumHandler.getPosts(mLocale);

                // Do we need to get a specific page here already
                if (arg0[0] > 1) {

                    posts = mForumHandler.getPosts(tempPageId, mLocale);
                }

                return (mThreadData != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (results && context != null) {

                // Let's set it up
                if (tempPageId > 1) {

                    mListAdapter.set(posts);

                } else {

                    mListAdapter.set(mThreadData.getPosts());

                }

                // Update the title
                mTextTitle.setText(mThreadData.getTitle());

                if (mThreadData.getNumPages() > 1) {

                    mWrapButtons.setVisibility(View.VISIBLE);
                    mButtonJump.setText(R.string.info_xml_feed_button_jump);
                    mButtonPrev.setEnabled(mCurrentPage > 1);
                    mButtonNext.setEnabled(mCurrentPage < mThreadData.getNumPages());
                    mButtonJump.setEnabled(true);

                } else {

                    mWrapButtons.setVisibility(View.GONE);
                    mButtonPrev.setEnabled(false);
                    mButtonNext.setEnabled(false);
                    mButtonJump.setEnabled(false);

                }

                // Do we need to hide?
                mSlidingDrawer.setVisibility(mThreadData.isLocked() ? View.GONE : View.VISIBLE);

                // Scroll to top
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Show the menu
        menu.add(0, 0, 0, R.string.info_profile_view);
        menu.add(0, 1, 0, R.string.info_forum_quote);
        menu.add(0, 2, 0, R.string.info_forum_links);
        menu.add(0, 3, 0, R.string.info_forum_report);

    }

    private void generatePopupWithLinks(String string) {

        // Got some?
        if (string == null) {
            Toast.makeText(mContext, R.string.info_forum_links_no,
                    Toast.LENGTH_SHORT).show();
        }

        // Init
        List<String> links = new ArrayList<String>();
        boolean linkFound = false;

        // Let's try to find 'em
        Pattern linkPattern = Pattern
                .compile("<a href=\"([^\"]+)\" rel=\"nofollow\">");
        Matcher linkMatcher = linkPattern.matcher(string);

        while (linkMatcher.find()) {

            linkFound = true;
            links.add(linkMatcher.group(1));

        }

        if (linkFound) {

            generateDialogLinkList(mContext, links).show();

        } else {

            // No links found
            Toast.makeText(mContext, R.string.info_forum_links_no,
                    Toast.LENGTH_SHORT).show();
        }

    }

    // Define the SlidingDrawer
    public void setupBottom(View v) {

        if (mSlidingDrawer == null) {

            mSlidingDrawer = (SlidingDrawer) v.findViewById(R.id.post_slider);

            // Set the drawer listeners
            mOnDrawerCloseListener = new OnDrawerCloseListener() {

                @Override
                public void onDrawerClosed() {
                    mSlidingDrawer.setClickable(false);
                }

            };
            mOnDrawerOpenListener = new OnDrawerOpenListener() {

                @Override
                public void onDrawerOpened() {
                    mSlidingDrawer.setClickable(true);
                }

            };

            // Attach the listeners
            mSlidingDrawer.setOnDrawerOpenListener(mOnDrawerOpenListener);
            mSlidingDrawer.setOnDrawerCloseListener(mOnDrawerCloseListener);

            // Grab the field + button for further reference!
            mTextareaContent = (EditText) v.findViewById(R.id.textarea_content);
            mButtonPost = (Button) v.findViewById(R.id.button_new);

        }

    }

    public void onPostSubmit(View v) {

        // Let's get the content
        String content = mTextareaContent.getText().toString();

        // Parse for the BBCODE!
        content = BBCodeUtils.toBBCode(content, mSelectedQuotes);

        // Ready... set... go!
        new AsyncPostInThread(mContext, mThreadData, mCaching).execute(content,
                mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, ""));

    }

    public void resetPostFields() {

        // Reset
        mTextareaContent.setText("");
        mSelectedQuotes.clear();
        mSlidingDrawer.animateClose();

    }

    public boolean onKeyDown(int keyCode) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mSlidingDrawer.isOpened()) {

                mSlidingDrawer.animateClose();
                return true;

            } else {

                return false;

            }

        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {

            startActivity(new Intent(mContext, ForumSearchActivity.class));
            return true;

        }

        return false;

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_next) {

            // Increment
            mCurrentPage++;

            // Let's do context
            new AsyncLoadPage(mContext, mThreadId).execute(mCurrentPage);

        } else if (v.getId() == R.id.button_prev) {

            // Increment
            mCurrentPage--;

            // Do the "get more"-thing
            new AsyncLoadPage(mContext, mThreadId).execute(mCurrentPage);

        } else if (v.getId() == R.id.button_jump) {

            generateDialogPage(mContext).show();

        }

    }

    public class AsyncLoadPage extends AsyncTask<Integer, Void, Boolean> {

        // Attributes
        private Context context;
        private long threadId;
        private int page;
        private List<ForumPostData> posts;

        // Constructs
        public AsyncLoadPage(Context c, long t) {

            this.context = c;
            this.threadId = t;

        }

        @Override
        protected void onPreExecute() {

            if (context instanceof ForumActivity) {

                mButtonJump.setText(R.string.label_downloading);
                mButtonJump.setEnabled(false);
                mButtonPrev.setEnabled(false);
                mButtonNext.setEnabled(false);

            }

        }

        @Override
        protected Boolean doInBackground(Integer... arg0) {

            try {

                page = arg0[0];
                mForumHandler.setThreadId(threadId);
                posts = mForumHandler.getPosts(page, mLocale);
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

                    mListAdapter.set(posts);
                    mButtonJump.setText(R.string.info_xml_feed_button_jump);

                } else {

                    Toast.makeText(context,
                            R.string.info_xml_threads_more_false,
                            Toast.LENGTH_SHORT).show();

                }

                mButtonPrev.setEnabled(page != 1);
                mButtonNext.setEnabled(page == mThreadData.getNumPages());
                mButtonJump.setEnabled(true);

            }

        }

    }

    public Dialog generateDialogLinkList(final Context context,
            final List<String> links) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_thread_link,
                (ViewGroup) getActivity().findViewById(R.id.dialog_root));

        // Set the title
        builder.setTitle(R.string.info_forum_link_title);

        // Dialog options
        builder.setNegativeButton(

                android.R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                }

                );

        // Padding fix
        final AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);

        // Grab the fields
        ListView listView = (ListView) layout.findViewById(R.id.list_links);
        listView.setAdapter(new ArrayAdapter<String>(context,
                R.layout.list_item_plain, android.R.id.text1, links));
        listView.setOnItemClickListener(

                new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {

                        // Get the current link
                        String currentLink = links.get(arg2);
                        new AsyncLinkHandling(context).execute(currentLink,
                                mSharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM,
                                        ""));

                        // Dismiss the dialog
                        theDialog.dismiss();

                    }

                }

                );

        return theDialog;

    }

    public Dialog generateDialogPage(final Context context) {

        // Attributes
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(
                R.layout.dialog_thread_page_select,
                (ViewGroup) ((Activity) context).findViewById(R.id.dialog_root));

        // Set the title and the view
        builder.setTitle(R.string.info_txt_pageselect);
        builder.setView(layout);

        // How many pages do we have?
        if (mPageArray == null || (mPageArray.length != mThreadData.getNumPages())) {

            mPageArray = new Integer[mThreadData.getNumPages()];
            for (int i = 0, max = mPageArray.length; i < max; i++) {

                mPageArray[i] = i;

            }

        }

        // Grab the elements
        final TextView textView = (TextView) layout
                .findViewById(R.id.text_desc);
        final EditText textPage = (EditText) layout
                .findViewById(R.id.text_page);

        // Set the text
        textView.setText(getString(R.string.info_xml_enternumber).replace(
                "{min}", "1")
                .replace("{max}", String.valueOf(mThreadData.getNumPages())));

        // Dialog options
        builder.setPositiveButton(

                android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String pageString = textPage.getText().toString();
                        if (!"".equals(pageString)) {

                            int page = Integer.parseInt(pageString);
                            if (0 < page && page <= mThreadData.getNumPages()) {

                                mCurrentPage = page;
                                new AsyncLoadPage(context, mThreadId)
                                        .execute(mCurrentPage);

                            } else {

                                Toast.makeText(context,
                                        R.string.info_forum_page_invalid,
                                        Toast.LENGTH_SHORT).show();

                            }

                        }

                    }

                }

                );

        // Padding fix
        AlertDialog theDialog = builder.create();
        theDialog.setView(layout, 0, 0, 0, 0);
        return theDialog;

    }

    public class AsyncLinkHandling extends AsyncTask<String, Void, Boolean> {

        // Attributes
        private Context context;
        private Intent intent;

        // Construct
        public AsyncLinkHandling(Context c) {

            context = c;

        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            // React accordingly
            String currentLink = arg0[0];
            int index = 0;

            if (currentLink.startsWith("http://battlelog.battlefield.com")) {

                try {

                    // *INBOUND LINKS*
                    int linkEndPos = currentLink.endsWith("/") ? currentLink
                            .length() - 1 : currentLink.length();
                    index = currentLink.indexOf("/user/");
                    if (index > -1) {

                        String username = currentLink.substring(index + 6,
                                linkEndPos);
                        intent = new Intent(context, ProfileActivity.class)
                                .putExtra(

                                        "profile",
                                        ProfileClient.getProfileIdFromName(username, arg0[1])

                                );

                    } else {

                        index = currentLink.indexOf("/platoon/");
                        if (index > -1) {

                            long platoonId = Long.parseLong(currentLink
                                    .substring(index + 9, linkEndPos));
                            intent = new Intent(context, PlatoonActivity.class)
                                    .putExtra(

                                            "platoon", new PlatoonData(platoonId, 0, 0,
                                                    0, null, null, null, true)

                                    );

                        } else {

                            index = currentLink.indexOf("/soldier/");
                            if (index > -1) {

                                currentLink = currentLink.substring(currentLink
                                        .indexOf("stats/") + 6);
                                long personaId = Long
                                        .parseLong(currentLink.substring(0,
                                                currentLink.indexOf('/')));
                                intent = new Intent(context, ProfileActivity.class)
                                        .putExtra(

                                                "profile",
                                                ProfileClient
                                                        .resolveFullProfileDataFromProfileId(personaId)

                                        );

                            } else {

                                index = currentLink
                                        .indexOf("forum/threadview/");
                                if (index > -1) {

                                    long threadId = Long.parseLong(currentLink
                                            .substring(index + 17, linkEndPos));
                                    intent = new Intent().putExtra(

                                            "threadId", threadId

                                            ).putExtra(

                                                    "threadTitle", "N/A"

                                            );
                                    openThread(intent);

                                } else {

                                    index = currentLink.indexOf("forum/view/");
                                    if (index > -1) {

                                        ((ForumActivity) context).openForum(new Intent().putExtra(
                                                "forumId",
                                                Long.parseLong(currentLink
                                                        .substring(index + 11,
                                                                linkEndPos))

                                                ).putExtra(

                                                        "forumTitle", "N/A"

                                                )

                                                );

                                    } else {

                                        intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(currentLink));

                                    }

                                }

                            }

                        }

                    }

                } catch (Exception ex) {

                    ex.printStackTrace();
                    return false;

                }

            } else {

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentLink));

            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {

                if (intent != null) {
                    startActivity(intent);
                }

            } else {

                Toast.makeText(context, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();

            }

        }

    }

    @Override
    public Menu prepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.option_save).setVisible(true);
        return menu;
    }

    @Override
    public boolean handleSelectedOption(MenuItem item) {

        if (item.getItemId() == R.id.option_save) {

            // Do we want to cache? Yes we want to do!
            new AsyncDoCache(mContext).execute();
            return true;
        }
        return false;
    }

    private class AsyncDoCache extends AsyncTask<Void, Void, Boolean> {

        // Attributes
        private Context context;

        public AsyncDoCache(Context c) {

            context = c;

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            return (CacheHandler.Forum.insert(context, mThreadData, SessionKeeper.getProfileData()
                    .getId()) > -1);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (context != null) {

                if (result) {

                    Toast.makeText(context, R.string.info_forum_save_true, Toast.LENGTH_SHORT)
                            .show();

                } else {

                    Toast.makeText(context, R.string.info_forum_save_false, Toast.LENGTH_SHORT)
                            .show();

                }

            }
        }

    }

    public void createContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {

        // Show the menu
        menu.add(0, 0, 0, R.string.info_profile_view);
        menu.add(0, 1, 0, R.string.info_forum_quote);
        menu.add(0, 2, 0, R.string.info_forum_links);
        menu.add(0, 3, 0, R.string.info_forum_report);

    }

    public boolean handleSelectedContextItem(AdapterView.AdapterContextMenuInfo info, MenuItem item) {

        try {

            // Let's get the item
            ForumPostData data = (ForumPostData) info.targetView.getTag();

            // Divide & conquer
            if (item.getGroupId() == 0) {

                // REQUESTS
                switch (item.getItemId()) {

                    case 0:
                        startActivity(new Intent(mContext, ProfileActivity.class).putExtra(
                                "profile", data.getProfileData()));
                        break;

                    case 1:
                        Toast.makeText(mContext, R.string.info_forum_quote_warning,
                                Toast.LENGTH_SHORT).show();
                        mTextareaContent.setText(

                                mTextareaContent.getText().insert(

                                        mTextareaContent.getSelectionStart(),
                                        BBCodeUtils.TAG_QUOTE_IN.replace(

                                                "{number}", String.valueOf(data.getPostId())

                                                ).replace(

                                                        "{username}",
                                                        data.getProfileData().getUsername()

                                                )

                                        )

                                );
                        mSelectedQuotes
                                .put(data.getPostId(),
                                        (data.isCensored() ? getString(R.string.general_censored)
                                                : data.getContent()));
                        break;

                    case 2:
                        generatePopupWithLinks(data.getContent());
                        break;

                    case 3:
                        startActivity(new Intent(mContext, ForumReportActivity.class)
                                .putExtra("postId", data.getPostId()));
                        break;

                    default:
                        Toast.makeText(mContext, R.string.msg_unimplemented,
                                Toast.LENGTH_SHORT).show();
                        break;

                }

            }

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }
}
