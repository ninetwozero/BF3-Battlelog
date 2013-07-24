package com.ninetwozero.bf3droid.util;

public class Platform {

    public static int resolveIdFromPlatformName(String platform) {
        if(platform.equalsIgnoreCase("pc")){
            return 1;
        } else if(platform.equalsIgnoreCase("xbox")) {
            return 2;
        } else if(platform.equalsIgnoreCase("ps3")) {
            return 4;
        } else {
            return 0;
        }
    }
}
