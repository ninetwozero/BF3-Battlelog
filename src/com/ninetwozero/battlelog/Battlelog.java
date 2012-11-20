package com.ninetwozero.battlelog;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.ninetwozero.battlelog.jsonmodel.personas.Soldier;
import com.ninetwozero.battlelog.misc.DebugLogConfig;
import java.util.List;

public class Battlelog extends Application {

    private static boolean isInDebugMode;
    public static final String NAME = "battlelog.db";
    private static final String BUGSENSE_KEY = "448b2f3b";

    private static Battlelog instance;
    public static final String AUTHORITY = "com.ninetwozero.battlelog.provider";
    private static List<Soldier> user;
    private static List<Soldier> guest;

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

    public static void setUser(List<Soldier> user){
        Battlelog.user = user;
    }

    public static List<Soldier> getUser(){
        return user;
    }

    public static List<Soldier> getGuest() {
        return guest;
    }

    public static void setGuest(List<Soldier> guest) {
        Battlelog.guest = guest;
    }
}
