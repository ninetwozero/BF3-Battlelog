package com.ninetwozero.battlelog.asynctask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.http.PlatoonClient;
import com.ninetwozero.battlelog.misc.Constants;

public class AsyncFetchDataToPlatoonView extends
        AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private SharedPreferences sharedPreferences;

    // Data
    PlatoonData platoon;

    // Error message
    private String error;

    public AsyncFetchDataToPlatoonView(Context c) {

        context = c;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        platoon = null;
        error = "";

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        // We got to do it the hard way - let's see if we can find a user with
        // that name.
        String searchString = arg0[0];

        try {

            // Post the world!
            platoon = PlatoonClient.getPlatoonId(

                    searchString,
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")

            );

            // Did we get an actual user?
            if (platoon == null || platoon.getId() == 0) {

                // Persona
                error = context.getString(R.string.msg_search_noplatoon)
                        .replace("{keyword}", searchString);
                return false;

            }

        } catch (Exception ex) {

            // D'oh
            error = context.getString(R.string.msg_search_noplatoon).replace(
                    "{keyword}", searchString);
            return false;

        }

        return true;

    }

    @Override
    protected void onPostExecute(Boolean result) {

        // How copy?
        if (result) {

            // To the Batmobile!
            context.startActivity(

                    new Intent(

                            context, PlatoonActivity.class

                    ).putExtra(

                            "platoon", platoon

                    )

            );

        } else {

            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

        }

    }

}
