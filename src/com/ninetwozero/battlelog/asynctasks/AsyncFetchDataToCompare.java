
package com.ninetwozero.battlelog.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ninetwozero.battlelog.CompareActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;
import com.ninetwozero.battlelog.handlers.ProfileHandler;
import com.ninetwozero.battlelog.misc.Constants;

public class AsyncFetchDataToCompare extends AsyncTask<String, Void, Boolean> {

    // Context
    private Context context;
    private SharedPreferences sharedPreferences;

    // Data
    private ProfileData playerOne, playerTwo;
    // Error message
    private String error;

    public AsyncFetchDataToCompare(Context c, ProfileData p) {

        context = c;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        playerOne = p;
        playerTwo = null;
        error = "";

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        // Get the first argument
        String searchString = arg0[0];

        try {

            // Post the world!
            playerTwo = ProfileHandler.getProfileId(

                    searchString,
                    sharedPreferences.getString(Constants.SP_BL_PROFILE_CHECKSUM, "")

                    );

            // Did we get an actual user?
            if (playerTwo == null || playerTwo.getNumPersonas() == 0) {

                // Persona
                error = context.getString(R.string.msg_search_nouser).replace(
                        "{keyword}", searchString);
                return false;

            }

        } catch (Exception ex) {

            // D'oh
            error = context.getString(R.string.msg_search_nouser).replace(
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

                            context, CompareActivity.class

                    ).putExtra(

                            "profile1", playerOne

                            ).putExtra(

                                    "profile2", playerTwo

                            )

                    );

        } else {

            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

        }

        return;
    }

}
