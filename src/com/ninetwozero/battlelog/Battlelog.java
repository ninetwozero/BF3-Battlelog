package com.ninetwozero.battlelog;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.ninetwozero.battlelog.misc.DebugLogConfig;

public class Battlelog extends Application {

    public static final String NAME = "battlelog.db";
    private static boolean isInDebugMode;

    private static Battlelog instance;
    public static final String AUTHORITY = "com.ninetwozero.battlelog.provider";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setDebugMode();
    }

    public static Context getContext() {
        return instance;
    }

    private void setDebugMode() {
        int debuggable = getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE;
        isInDebugMode = debuggable > 0;
        if (isInDebugMode) {
            setupDebugOptions();
        } else {
            setupReleaseOptions();
        }
    }

    private void setupDebugOptions() {
        DebugLogConfig.enable();
    }

    private void setupReleaseOptions() {
        //BugSenseHandler.setup(this, BUGSENSE_KEY);
    }
}
