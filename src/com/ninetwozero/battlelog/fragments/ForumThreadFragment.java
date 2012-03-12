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

package com.ninetwozero.battlelog.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.view.MenuItem;
import android.view.View;
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

import com.ninetwozero.battlelog.Backup_ForumView;
import com.ninetwozero.battlelog.ForumReportView;
import com.ninetwozero.battlelog.ForumSearchView;
import com.ninetwozero.battlelog.ForumThreadView;
import com.ninetwozero.battlelog.ForumView;
import com.ninetwozero.battlelog.PlatoonView;
import com.ninetwozero.battlelog.ProfileView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.ThreadPostListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncPostInThread;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.PlatoonData;
import com.ninetwozero.battlelog.misc.BBCodeUtils;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumThreadFragment extends ListFragment {

    // Attributes
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private Board.ThreadData threadData;

    // Elements
    private ListView listView;
    private ThreadPostListAdapter listAdapter;
    private TextView textTitle;
    private RelativeLayout wrapButtons, wrapLoader;
    private Button buttonPost, buttonPrev, buttonNext, buttonJump;
    private SlidingDrawer slidingDrawer;
    private TextView slidingDrawerHandle;
    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerCloseListener onDrawerCloseListener;
    private EditText textareaContent;

    // Misc
    private long threadId;
    private long latestRefresh;
    private String locale;
    private int currentPage;
    private HashMap<Long, String> selectedQuotes;
    private Integer[] pageArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        context = getActivity();
        layoutInflater = inflater;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        // Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.forum_thread_view,
                container, false);

        // Get the unlocks
        locale = sharedPreferences.getString(Constants.SP_BL_LOCALE, "en");

        // Init the views
        initViews(view);
        setupBottom(view);

        // Init data
        initData();

        // Return the view
        return view;

    }

    public void initData() {

        // Init the quotes and page
        if (selectedQuotes == null) {
            selectedQuotes = new HashMap<Long, String>();
        }
        if (currentPage == 0) {
            currentPage = 1;
        }

    }

    public void initViews(View v) {

        // Setup the text
        textTitle = (TextView) v.findViewById(R.id.text_title_thread);

        // Setup the ListView
        listView = (ListView) v.findViewById(android.R.id.list);
        listView.setAdapter(listAdapter = new ThreadPostListAdapter(context, null, layoutInflater));
        listView.setDrawSelectorOnTop(false);
        registerForContextMenu(listView);

        // Let's get the button
        buttonJump = (Button) v.findViewById(R.id.button_jump);
        buttonPrev = (Button) v.findViewById(R.id.button_prev);
        buttonNext = (Button) v.findViewById(R.id.button_next);
        wrapButtons = (RelativeLayout) v.findViewById(R.id.wrap_buttons);

        // Last but not least, the loader
        wrapLoader = (RelativeLayout) v.findViewById(R.id.wrap_loader);

    }

    @Override
    public void onResume() {

        super.onResume();
        reload();

    }

    public void reload() {

        // Do we have a forumId?
        if (threadId == 0) {
            return;
        }

        // Set it up
        if (threadData == null) {

            new AsyncGetPosts(context).execute(threadId);

        } else {

            new AsyncLoadPage(context, threadId).execute(currentPage);

        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        getActivity().openContextMenu(v);

    }

    public void openThread(Intent data) {

        threadId = data.getLongExtra("threadId", 0);
        textTitle.setText(data.getStringExtra("threadTitle"));
        reload();

    }

    private class AsyncGetPosts extends AsyncTask<Long, Void, Boolean> {

        // Attributes
        private Context context;

        // Construct
        public AsyncGetPosts(Context c) {

            context = c;

        }

        @Override
        protected void onPreExecute() {

            if (context != null) {

                RotateAnimation rotateAnimation = new RotateAnimation(0, 359,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                wrapLoader.setVisibility(View.VISIBLE);
                wrapLoader.findViewById(R.id.image_loader).startAnimation(rotateAnimation);

            }

        }

        @Override
        protected Boolean doInBackground(Long... arg0) {

            try {

                threadData = WebsiteHandler.getPostsForThread(locale,
                        arg0[0]);
                return (threadData != null);

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (results) {

                if (context != null) {

                    // Let's set it up
                    listAdapter.set(threadData.getPosts());

                    // Update the title
                    textTitle.setText(threadData.getTitle());

                    if (threadData.getNumPages() > 1) {

                        wrapButtons.setVisibility(View.VISIBLE);
                        buttonJump
                                .setText(getString(R.string.info_xml_feed_button_jump));
                        buttonPrev.setEnabled(false);
                        buttonNext.setEnabled(true);
                        buttonJump.setEnabled(true);

                    } else {

                        wrapButtons.setVisibility(View.GONE);
                        buttonPrev.setEnabled(false);
                        buttonNext.setEnabled(false);
                        buttonJump.setEnabled(false);

                    }

                    // Do we need to hide?
                    if (threadData.isLocked()) {
                        slidingDrawer.setVisibility(View.GONE);
                    } else {
                        slidingDrawer.setVisibility(View.VISIBLE);
                    }

                }

            }

            // Hide it
            // wrapLoader.setVisibility( View.GONE );

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

        // RETURN
        return;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // Declare...
        AdapterView.AdapterContextMenuInfo info;

        // Let's try to get some menu information via a try/catch
        try {

            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        } catch (ClassCastException e) {

            e.printStackTrace();
            return false;

        }

        try {

            // Let's get the item
            Board.PostData data = (Board.PostData) info.targetView.getTag();

            // Divide & conquer
            if (item.getGroupId() == 0) {

                // REQUESTS
                switch (item.getItemId()) {

                    case 0:
                        startActivity(new Intent(context, ProfileView.class).putExtra(
                                "profile", data.getProfileData()));
                        break;

                    case 1:
                        Toast.makeText(context, R.string.info_forum_quote_warning,
                                Toast.LENGTH_SHORT).show();
                        textareaContent.setText(

                                textareaContent.getText().insert(

                                        textareaContent.getSelectionStart(),
                                        Constants.BBCODE_TAG_QUOTE_IN.replace(

                                                "{number}", data.getPostId() + ""

                                                ).replace(

                                                        "{username}",
                                                        data.getProfileData().getAccountName()

                                                )

                                        )

                                );
                        selectedQuotes
                                .put(data.getPostId(),
                                        (data.isCensored() ? getString(R.string.general_censored)
                                                : data.getContent()));
                        break;

                    case 2:
                        generatePopupWithLinks(data.getContent());
                        break;

                    case 3:
                        startActivity(new Intent(context, ForumReportView.class)
                                .putExtra("postId", data.getPostId()));
                        break;

                    default:
                        Toast.makeText(context, R.string.msg_unimplemented,
                                Toast.LENGTH_SHORT).show();
                        break;

                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

        return true;

    }

    private void generatePopupWithLinks(String string) {

        // Got some?
        if (string == null) {
            Toast.makeText(context, R.string.info_forum_links_no,
                    Toast.LENGTH_SHORT).show();
        }

        // Init
        ArrayList<String> links = new ArrayList<String>();
        boolean linkFound = false;

        // Let's try to find 'em
        Pattern linkPattern = Pattern
                .compile("<a href=\"([^\"]+)\" rel=\"nofollow\">");
        Matcher linkMatcher = linkPattern.matcher(string);

        while (linkMatcher.find()) {

            linkFound = true;
            links.add(linkMatcher.group(1));

        }

        if (!linkFound) {

            // No links found
            Toast.makeText(context, R.string.info_forum_links_no,
                    Toast.LENGTH_SHORT).show();

        } else {

            generateDialogLinkList(context, links).show();

        }

    }

    // Define the SlidingDrawer
    public void setupBottom(View v) {

        if (slidingDrawer == null) {

            slidingDrawer = (SlidingDrawer) v.findViewById(R.id.post_slider);
            slidingDrawerHandle = (TextView) v.findViewById(R.id.post_slide_handle_text);

            // Set the drawer listeners
            onDrawerCloseListener = new OnDrawerCloseListener() {

                @Override
                public void onDrawerClosed() {
                    slidingDrawer.setClickable(false);
                }

            };
            onDrawerOpenListener = new OnDrawerOpenListener() {

                @Override
                public void onDrawerOpened() {
                    slidingDrawer.setClickable(true);
                }

            };

            // Attach the listeners
            slidingDrawer.setOnDrawerOpenListener(onDrawerOpenListener);
            slidingDrawer.setOnDrawerCloseListener(onDrawerCloseListener);

            // Grab the field + button for further reference!
            textareaContent = (EditText) v.findViewById(R.id.textarea_content);
            buttonPost = (Button) v.findViewById(R.id.button_new);

        }

    }

    public void onPostSubmit(View v) {

        // Let's get the content
        String content = textareaContent.getText().toString();

        // Parse for the BBCODE!
        content = BBCodeUtils.toBBCode(content, selectedQuotes);

        // Ready... set... go!
        new AsyncPostInThread(context, threadId).execute(content,
                sharedPreferences.getString(Constants.SP_BL_CHECKSUM, ""));

    }

    public void resetPostFields() {

        // Reset
        textareaContent.setText("");
        selectedQuotes.clear();
        slidingDrawer.animateClose();

    }

    public boolean onKeyDown(int keyCode) {

        // Hotkeys
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (slidingDrawer.isOpened()) {

                slidingDrawer.animateClose();
                return true;

            } else {

                return false;

            }

        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {

            startActivity(new Intent(context, ForumSearchView.class));
            return true;

        }

        return false;

    }

    public void onClick(View v) {

        if (v.getId() == R.id.button_next) {

            // Increment
            currentPage++;

            // Let's do context
            new AsyncLoadPage(context, threadId).execute(currentPage);

        } else if (v.getId() == R.id.button_prev) {

            // Increment
            currentPage--;

            // Do the "get more"-thing
            new AsyncLoadPage(context, threadId).execute(currentPage);

        } else if (v.getId() == R.id.button_jump) {

            generateDialogPage(context).show();

        }

    }

    public class AsyncLoadPage extends AsyncTask<Integer, Void, Boolean> {

        // Attributes
        private Context context;
        private long threadId;
        private int page;
        private List<Board.PostData> posts;

        // Constructs
        public AsyncLoadPage(Context c, long t) {

            this.context = c;
            this.threadId = t;

        }

        @Override
        protected void onPreExecute() {

            if (context instanceof ForumView) {

                buttonJump.setText(getString(R.string.label_downloading));
                buttonJump.setEnabled(false);
                buttonPrev.setEnabled(false);
                buttonNext.setEnabled(false);

            }

        }

        @Override
        protected Boolean doInBackground(Integer... arg0) {

            try {

                page = arg0[0];
                posts = WebsiteHandler.getPostsForThread(this.threadId, page,
                        locale);
                return true;

            } catch (Exception ex) {

                ex.printStackTrace();
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean results) {

            if (context instanceof ForumView) {

                if (results) {

                    listAdapter.set(posts);
                    buttonJump.setText(R.string.info_xml_feed_button_jump);

                } else {

                    Toast.makeText(context,
                            R.string.info_xml_threads_more_false,
                            Toast.LENGTH_SHORT).show();

                }

                if (page != 1) {
                    buttonPrev.setEnabled(true);
                } else {
                    buttonPrev.setEnabled(false);
                }
                if (page != threadData.getNumPages()) {
                    buttonNext.setEnabled(true);
                } else {
                    buttonNext.setEnabled(false);
                }
                buttonJump.setEnabled(true);

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
                                sharedPreferences.getString(Constants.SP_BL_CHECKSUM,
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
        if (pageArray == null || (pageArray.length != threadData.getNumPages())) {

            pageArray = new Integer[threadData.getNumPages()];
            for (int i = 0, max = pageArray.length; i < max; i++) {

                pageArray[i] = i;

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
                .replace("{max}", threadData.getNumPages() + ""));

        // Dialog options
        builder.setPositiveButton(

                android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String pageString = textPage.getText().toString();
                        if (!pageString.equals("")) {

                            int page = Integer.parseInt(pageString);
                            if (0 < page && page <= threadData.getNumPages()) {

                                currentPage = page;
                                new AsyncLoadPage(context, threadId)
                                        .execute(currentPage);

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
                        intent = new Intent(context, ProfileView.class)
                                .putExtra(

                                        "profile", WebsiteHandler
                                                .getProfileIdFromSearch(username,
                                                        arg0[1])

                                );

                    } else {

                        index = currentLink.indexOf("/platoon/");
                        if (index > -1) {

                            long platoonId = Long.parseLong(currentLink
                                    .substring(index + 9, linkEndPos));
                            intent = new Intent(context, PlatoonView.class)
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
                                intent = new Intent(context, ProfileView.class)
                                        .putExtra(

                                                "profile",
                                                WebsiteHandler
                                                        .getProfileIdFromPersona(personaId)

                                        );

                            } else {

                                index = currentLink
                                        .indexOf("forum/threadview/");
                                if (index > -1) {

                                    long threadId = Long.parseLong(currentLink
                                            .substring(index + 17, linkEndPos));
                                    intent = new Intent(context,
                                            ForumThreadView.class).putExtra(

                                            "threadId", threadId

                                            ).putExtra(

                                                    "threadTitle", "N/A"

                                            );

                                } else {

                                    index = currentLink.indexOf("forum/view/");
                                    if (index > -1) {

                                        intent = new Intent(context,
                                                Backup_ForumView.class).putExtra(

                                                "forumId",
                                                Long.parseLong(currentLink
                                                        .substring(index + 11,
                                                                linkEndPos))

                                                ).putExtra(

                                                        "forumTitle", "N/A"

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
}
