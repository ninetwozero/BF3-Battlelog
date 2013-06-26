package com.ninetwozero.bf3droid.util;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatter {

    public static String timeString(int timeValue) {
        Time time = new Time();
        time.switchTimezone("UTC");
        time.set(timeValue * 1000);
        return time.format("%kh%Mm");
    }

    public static String dateString(long dateValue) {
        Date date = new Date(dateValue * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
