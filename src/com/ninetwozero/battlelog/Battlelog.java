package com.ninetwozero.battlelog;

import android.app.Application;
import android.content.Context;

public class Battlelog extends Application{

    public static final String NAME = "battlelog.db";

    public static class RankProgress{

    }

    private static Battlelog instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext(){
        return instance;
    }
}
