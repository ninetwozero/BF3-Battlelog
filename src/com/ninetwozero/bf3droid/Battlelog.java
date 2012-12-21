package com.ninetwozero.bf3droid;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.misc.DebugLogConfig;

import java.util.List;

public class Battlelog extends Application {

    private static boolean isInDebugMode;
    public static final String NAME = "battlelog.db";
    private static final String BUGSENSE_KEY = "448b2f3b";

    private static Battlelog instance;
    public static final String AUTHORITY = "com.ninetwozero.battlelog.provider";
    private static String user;
    private static String checkSum;
    private static long userId;
    private static List<SimplePersona> userPersonas;
    private static List<SimplePersona> guestPersonas;
    private static long selectedUserPersona = 0;
    private static long selectedGuestPersona = 0;
    private static List<SimplePlatoon> userPlatoons;
    private static List<SimplePlatoon> guestPlatoons;
    private static long selectedUserPlatoon = 0;
    private static long getSelectedGuestPersona = 0;

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

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        Battlelog.user = user;
    }

    public static String getCheckSum() {
        return checkSum;
    }

    public static void setCheckSum(String checkSum) {
        Battlelog.checkSum = checkSum;
    }

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long userId) {
        Battlelog.userId = userId;
    }

    public static void setUserPersonas(List<SimplePersona> userPersonas){
        Battlelog.userPersonas = userPersonas;
    }

    public static List<SimplePersona> getUserPersonas(){
        return userPersonas;
    }

    public static List<SimplePersona> getGuestPersonas() {
        return guestPersonas;
    }

    public static void setGuestPersonas(List<SimplePersona> guestPersonas) {
        Battlelog.guestPersonas = guestPersonas;
    }

    public static void setSelectedUserPersona(long selectedUserPersona) {
        Battlelog.selectedUserPersona = selectedUserPersona;
    }

    public static void setSelectedGuestPersona(long selectedGuestPersona) {
        Battlelog.selectedGuestPersona = selectedGuestPersona;
    }

    public static SimplePersona selectedUserPersona(){
        if(selectedUserPersona == 0){
            return userPersonas.get(0);
        }
        for (SimplePersona simplePersona: userPersonas){
            if (simplePersona.getPersonaId() == selectedUserPersona){
                return simplePersona;
            }
        }
        return userPersonas.get(0);
    }

    public static void setUserPlatoons(List<SimplePlatoon> platoons) {
        Battlelog.userPlatoons = platoons;
    }

    public static List<SimplePlatoon> getUserPlatoons() {
        return userPlatoons;
    }

    public static SimplePlatoon selectedUserPlatoon() {
        if(selectedUserPlatoon == 0){
            return userPlatoons.get(0);
        }
        for(SimplePlatoon simplePlatoon : userPlatoons){
            if(simplePlatoon.getPlatoonId() == selectedUserPlatoon){
                return  simplePlatoon;
            }
        }
        return userPlatoons.get(0);
    }
}
