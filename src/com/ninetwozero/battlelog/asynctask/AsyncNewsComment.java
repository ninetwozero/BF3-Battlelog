package com.ninetwozero.battlelog.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.news.CommentListFragment;
import com.ninetwozero.battlelog.datatype.CommentData;
import com.ninetwozero.battlelog.datatype.NewsData;
import com.ninetwozero.battlelog.http.CommentClient;

public class AsyncNewsComment extends AsyncTask<String, Void, Boolean> {

    // Attributes
    private Context context;
    private CommentListFragment fragmentComments;
    private NewsData newsData;

    // Elements
    private Button buttonSend;

    public AsyncNewsComment(Context c, NewsData n, CommentListFragment f) {

        context = c;
        newsData = n;
        fragmentComments = f;

    }

    @Override
    protected void onPreExecute() {

        if (context != null) {

            buttonSend = (Button) fragmentComments.getView().findViewById(
                    R.id.button_send);
            buttonSend.setText(R.string.label_sending);
            buttonSend.setEnabled(false);

        }

    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            return new CommentClient(newsData.getId(), CommentData.TYPE_NEWS)
                    .post(arg0[0], arg0[1]);

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
            ((EditText) fragmentComments.getView().findViewById(
                    R.id.field_message)).setText("");

            Toast.makeText(context, R.string.info_news_comment_true,
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(context, R.string.info_news_comment_false,
                    Toast.LENGTH_SHORT).show();

        }

    }

}
