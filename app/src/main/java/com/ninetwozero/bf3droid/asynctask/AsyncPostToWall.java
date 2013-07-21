package com.ninetwozero.bf3droid.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.feed.FeedFragment;
import com.ninetwozero.bf3droid.http.FeedClient;

public class AsyncPostToWall extends AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private boolean isPlatoon;

    // Data
    private FeedFragment fragmentFeed;
    private long profileId;

    // Elements
    private Button buttonSend;

    public AsyncPostToWall(Context c, long pId, boolean p, FeedFragment f) {

        context = c;
        profileId = pId;
        isPlatoon = p;
        fragmentFeed = f;

    }

    @Override
    protected void onPreExecute() {

        if (context != null) {

            buttonSend = (Button) fragmentFeed.getView().findViewById(
                    R.id.button_send);
            buttonSend.setText(R.string.label_sending);
            buttonSend.setEnabled(false);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            return new FeedClient(profileId,
                    isPlatoon ? FeedClient.TYPE_PLATOON
                            : FeedClient.TYPE_PROFILE).post(arg0[0], arg0[1]);

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

            // Let's reload
            fragmentFeed.reload();

            buttonSend.setText(R.string.label_send);
            buttonSend.setEnabled(true);
            ((EditText) fragmentFeed.getView().findViewById(R.id.field_message))
                    .setText("");

            Toast.makeText(context, R.string.msg_feed_ok, Toast.LENGTH_SHORT)
                    .show();
        } else {

            Toast.makeText(context, R.string.msg_feed_fail, Toast.LENGTH_SHORT)
                    .show();

        }

    }

}
