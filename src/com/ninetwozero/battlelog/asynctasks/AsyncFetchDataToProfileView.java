
package com.ninetwozero.battlelog.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.ProfileActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.handlers.ProfileHandler;
import com.ninetwozero.battlelog.misc.Constants;

public class AsyncFetchDataToProfileView extends
        AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private SharedPreferences sharedPreferences;

    // Data
    private ProfileData userData;

    // Error message
    private String error;

    public AsyncFetchDataToProfileView(Context c) {

        context = c;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        userData = null;
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
            userData = ProfileHandler.getProfileId(

                    searchString,
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")

                    );

            // Did we get an actual user?
            if (userData == null || userData.getNumPersonas() == 0) {

                // Persona
                error = context.getString(R.string.msg_search_nouser)
                        + searchString;
                return false;

            }

        } catch (Exception ex) {

            // D'oh
            error = context.getString(R.string.msg_search_nouser)
                    + searchString;
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

                            context, ProfileActivity.class

                    ).putExtra(

                            "profile", userData

                            )

                    );

        } else {

            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

        }

        return;
    }

}
