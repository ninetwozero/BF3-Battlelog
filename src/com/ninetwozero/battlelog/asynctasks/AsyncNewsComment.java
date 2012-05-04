
package com.ninetwozero.battlelog.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.NewsData;
import com.ninetwozero.battlelog.fragments.NewsCommentListFragment;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class AsyncNewsComment extends AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private Activity origin;
    private SharedPreferences sharedPreferences;
    private boolean isPlatoon;

    // Data
    private NewsCommentListFragment fragmentComments;
    private NewsData newsData;

    // Elements
    private Button buttonSend;

    // Error message
    private String error;

    public AsyncNewsComment(Context c, NewsData n, NewsCommentListFragment f) {

        context = c;
        origin = (Activity) context;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        newsData = n;
        fragmentComments = f;

    }

    @Override
    protected void onPreExecute() {

        if (context != null) {

            buttonSend = (Button) fragmentComments.getView().findViewById(R.id.button_send);
            buttonSend.setText(R.string.label_sending);
            buttonSend.setEnabled(false);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            return WebsiteHandler.commentOnNews(arg0[0], newsData, arg0[1]);

        } catch (Exception ex) {

            // D'oh
            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean result) {

        // How'd it go?
        if (result) {
            fragmentComments.reload();

            buttonSend.setText(R.string.label_send);
            buttonSend.setEnabled(true);
            ((EditText) fragmentComments.getView().findViewById(R.id.field_message)).setText("");

            Toast.makeText(context, R.string.info_news_comment_true, Toast.LENGTH_SHORT)
                    .show();
        } else {

            Toast.makeText(context, R.string.info_news_comment_false, Toast.LENGTH_SHORT)
                    .show();

        }

    }

}
