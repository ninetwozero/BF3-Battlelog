package com.ninetwozero.bf3droid.util;

import android.text.format.Time;

public class TimeFormatter {

    public static String timeString(int timeValue) {
        Time time = new Time();
        time.switchTimezone("UTC");
        time.set(timeValue * 1000);
        return time.format("%kh%Mm");
    }
}
