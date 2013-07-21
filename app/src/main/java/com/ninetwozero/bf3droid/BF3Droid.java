package com.ninetwozero.bf3droid;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.ninetwozero.bf3droid.misc.DebugLogConfig;
import com.ninetwozero.bf3droid.model.User;

public class BF3Droid extends Application {

    private static boolean isInDebugMode;
    public static final String NAME = "bf3droid.db";
    private static final String BUGSENSE_KEY = "448b2f3b";

    private static BF3Droid instance;
    public static final String AUTHORITY = "com.ninetwozero.bf3droid.provider";
    private static String checkSum;
    private static User user;
    private static User guest;

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

    public static String getCheckSum() {
        return checkSum;
    }

    public static void setCheckSum(String checkSum) {
        BF3Droid.checkSum = checkSum;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        BF3Droid.user = user;
    }

    public static User getGuest() {
        return guest;
    }

    public static void setGuest(User guest) {
        BF3Droid.guest = guest;
    }

    public static User getUserBy(String user){
        if(user.equals(User.USER)){
            return getUser();
        } else {
            return getGuest();
        }
    }
}
