/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ninetwozero.battlelog.PlatoonActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.handlers.PlatoonHandler;

public class AsyncPlatoonRespond extends AsyncTask<String, Integer, Boolean> {

    // Attribute
    private Context context;
    private long platoonId, profileId;
    private boolean response;

    // Constructor
    public AsyncPlatoonRespond(Context c, long plId, long pId, boolean r) {

        context = c;
        platoonId = plId;
        profileId = pId;
        response = r;

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(String... arg0) {

        try {

            // Let's get this!!
            return PlatoonHandler.answerPlatoonRequest(platoonId, profileId,
                    response, arg0[0]);

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean results) {

        // Let the user know and then refresh!
        if (context != null) {

            Toast.makeText(
                    context,
                    results ? R.string.info_platoon_req_ok : R.string.info_platoon_req_fail,
                    Toast.LENGTH_SHORT
                    ).show();

            ((PlatoonActivity) context).reload();
        }
        return;

    }

}
