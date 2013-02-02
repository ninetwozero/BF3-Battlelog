package com.ninetwozero.bf3droid;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.misc.DebugLogConfig;

import java.util.List;

import org.apache.http.cookie.Cookie;

public class BF3Droid extends Application {

    private static boolean isInDebugMode;
    public static final String NAME = "bf3droid.db";
    private static final String BUGSENSE_KEY = "448b2f3b";

    private static BF3Droid instance;
    public static final String AUTHORITY = "com.ninetwozero.bf3droid.provider";
    private static String user;
    private static String checkSum;
    private static Cookie cookie;
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
        BF3Droid.user = user;
    }

    public static boolean hasUser(){
        return getUser() != null;
    }

    public static String getCheckSum() {
        return checkSum;
    }

    public static void setCheckSum(String checkSum) {
        BF3Droid.checkSum = checkSum;
    }

    public static Cookie getCookie() {
        return cookie;
    }

    public static boolean hasCookie(){
        return getCookie() != null;
    }

    public static void setCookie(Cookie cookie) {
        BF3Droid.cookie = cookie;
    }

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long userId) {
        BF3Droid.userId = userId;
    }

    public static void setUserPersonas(List<SimplePersona> userPersonas){
        BF3Droid.userPersonas = userPersonas;
    }

    public static List<SimplePersona> getUserPersonas(){
        return userPersonas;
    }

    public static List<SimplePersona> getGuestPersonas() {
        return guestPersonas;
    }

    public static void setGuestPersonas(List<SimplePersona> guestPersonas) {
        BF3Droid.guestPersonas = guestPersonas;
    }

    public static void setSelectedUserPersona(long selectedUserPersona) {
        BF3Droid.selectedUserPersona = selectedUserPersona;
    }

    public static void setSelectedGuestPersona(long selectedGuestPersona) {
        BF3Droid.selectedGuestPersona = selectedGuestPersona;
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
        BF3Droid.userPlatoons = platoons;
    }

    public static List<SimplePlatoon> getUserPlatoons() {
        return userPlatoons;
    }

    public static void setSelectedUserPlatoon(long selectedUserPlatoon){
        BF3Droid.selectedUserPlatoon = selectedUserPlatoon;
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
