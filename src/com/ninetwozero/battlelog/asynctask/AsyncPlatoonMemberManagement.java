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

package com.ninetwozero.battlelog.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.ninetwozero.battlelog.activity.platoon.PlatoonActivity;
import com.ninetwozero.battlelog.datatype.PlatoonData;
import com.ninetwozero.battlelog.http.PlatoonClient;

public class AsyncPlatoonMemberManagement extends
        AsyncTask<Boolean, Void, Boolean> {

    // Attributes
    private Context context;
    private PlatoonData platoonData;
    private long userId;

    // Constructs
    public AsyncPlatoonMemberManagement(Context c, long uId, PlatoonData p) {

        context = c;
        userId = uId;
        platoonData = p;

    }

    @Override
    protected Boolean doInBackground(Boolean... arg0) {

        try {

            if (arg0.length == 0) {

                return new PlatoonClient(platoonData).editMember(userId, PlatoonClient.FILTER_KICK);

            } else if (arg0[0]) {

                return new PlatoonClient(platoonData).editMember(userId,
                        PlatoonClient.FILTER_PROMOTE);

            } else {
                
                return new PlatoonClient(platoonData).editMember(userId,
                        PlatoonClient.FILTER_DEMOTE);
                
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (context instanceof PlatoonActivity) {

            ((PlatoonActivity) context).reload();

        }

    }

}
