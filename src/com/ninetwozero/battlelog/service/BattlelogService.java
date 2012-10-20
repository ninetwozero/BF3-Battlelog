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

package com.ninetwozero.battlelog.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.ninetwozero.battlelog.asynctask.AsyncServiceTask;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class BattlelogService extends Service {

    // Attributes
    private SharedPreferences mSharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (PublicUtils.isNetworkAvailable(this)) {
            new AsyncServiceTask(this, mSharedPreferences).execute();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startid) {}

}
