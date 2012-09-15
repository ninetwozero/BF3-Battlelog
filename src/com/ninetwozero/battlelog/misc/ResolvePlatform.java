package com.ninetwozero.battlelog.misc;

public class ResolvePlatform {

    public static String platformName(int id){
        switch (id) {
            case 0:
            case 1:
                return "[PC]";
            case 2:
                return "[360]";
            case 4:
                return "[PS3]";
            default:
                return "[N/A]";
        }
    }
}
