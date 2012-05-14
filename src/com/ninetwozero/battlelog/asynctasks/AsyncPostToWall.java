
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
import com.ninetwozero.battlelog.fragments.FeedFragment;
import com.ninetwozero.battlelog.handlers.WebsiteHandler;

public class AsyncPostToWall extends AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private Activity origin;
    private SharedPreferences sharedPreferences;
    private boolean isPlatoon;

    // Data
    private FeedFragment fragmentFeed;
    private long profileId;

    // Elements
    private Button buttonSend;

    // Error message
    private String error;

    public AsyncPostToWall(Context c, long pId, boolean p, FeedFragment f) {

        context = c;
        origin = (Activity) context;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        profileId = pId;
        isPlatoon = p;
        fragmentFeed = f;

    }

    @Override
    protected void onPreExecute() {

        if (context != null) {

            buttonSend = (Button) fragmentFeed.getView().findViewById(R.id.button_send);
            buttonSend.setText(R.string.label_sending);
            buttonSend.setEnabled(false);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            return WebsiteHandler.postToWall(profileId, arg0[0], arg0[1],
                    isPlatoon);

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
            if (!isPlatoon) {
                fragmentFeed.reload();
            } else {
                fragmentFeed.reload();
            }

            buttonSend.setText(R.string.label_send);
            buttonSend.setEnabled(true);
            ((EditText) fragmentFeed.getView().findViewById(R.id.field_message)).setText("");

            Toast.makeText(context, R.string.msg_feed_ok, Toast.LENGTH_SHORT)
                    .show();
        } else {

            Toast.makeText(context, R.string.msg_feed_fail, Toast.LENGTH_SHORT)
                    .show();

        }

    }

}
