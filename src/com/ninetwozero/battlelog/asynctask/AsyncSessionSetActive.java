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

import android.os.AsyncTask;

import com.ninetwozero.battlelog.datatype.PostData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.COMClient;

public class AsyncSessionSetActive extends
        AsyncTask<PostData, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(PostData... arg0) {

        try {

            return COMClient.setActive();

        } catch (WebsiteHandlerException e) {

            return false;

        }

    }

}
