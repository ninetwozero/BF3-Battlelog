package com.ninetwozero.bf3droid.misc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    public static String format(long num) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        return nf.format(num);
    }

    public static String format(double num) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.applyPattern("###.##");
        return df.format(num);
    }
}
