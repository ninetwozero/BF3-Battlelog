/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninetwozero.battlelog.misc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import com.coveragemapper.android.Map.ExternalCacheDirectory;
import com.ninetwozero.battlelog.MainActivity;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.asynctask.AsyncSessionSetActive;
import com.ninetwozero.battlelog.asynctask.AsyncSessionValidate;
import com.ninetwozero.battlelog.datatype.ShareableCookie;
import com.ninetwozero.battlelog.http.RequestHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PublicUtils {

    public static String getDate(final Long d) {

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(d * 1000));

    }

    /**
     * <p>
     * Get the "relative" date
     * </p>
     *
     * @param d the first String, must not be null
     * @param s the second String, must not be null
     * @return String the relative date
     */

    public static final String getDate(long d, String s) {

        return s + " " + getDate(d);

    }

    public static String getRelativeDate(final Context c, final Long d) {

        // Let's just expect it to be millis seconds already
        Long dateStart = d;
        Long dateNow = System.currentTimeMillis() / 1000;
        Long dateDiff = dateNow - dateStart;
        String dateString = null;

        // When did we start? 0 == just one login (if I haven't understood it
        // too wrong)
        if (d == 0) {
            return "an unknown amount of time ago.";
        }

        // Diff is not allowed to be < 0
        dateDiff = (dateDiff < 0) ? 0 : dateDiff;

        // What's the difference (in seconds) y'all?
        if ((dateDiff / Constants.MINUTE_IN_SECONDS) < 1) {

            // Diff is in seconds
            if (dateDiff == 1) {

                dateString = c.getString(R.string.info_time_second).replace(
                        "{seconds}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_second_p).replace(
                        "{seconds}",
                        String.valueOf(dateDiff % Constants.MINUTE_IN_SECONDS));

            }

        } else if ((dateDiff / Constants.HOUR_IN_SECONDS) < 1) {

            // Diff is in minutes
            if ((dateDiff / Constants.MINUTE_IN_SECONDS) == 1) {

                dateString = c.getString(R.string.info_time_min).replace(
                        "{minutes}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_min_p).replace(
                        "{minutes}",
                        String.valueOf(dateDiff / Constants.MINUTE_IN_SECONDS));

            }
        } else if ((dateDiff / Constants.DAY_IN_SECONDS) < 1) {

            // Diff is in hours
            if ((dateDiff / Constants.HOUR_IN_SECONDS) == 1) {

                dateString = c.getString(R.string.info_time_hour).replace(
                        "{hours}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_hour_p).replace(
                        "{hours}",
                        String.valueOf(dateDiff / Constants.HOUR_IN_SECONDS));

            }

        } else if ((dateDiff / Constants.WEEK_IN_SECONDS) < 1) {

            // Diff is in days
            if ((dateDiff / Constants.DAY_IN_SECONDS) == 1) {

                dateString = c.getString(R.string.info_time_day).replace(
                        "{days}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_day_p).replace(
                        "{days}",
                        String.valueOf(dateDiff / Constants.DAY_IN_SECONDS));

            }

        } else if ((dateDiff / Constants.YEAR_IN_SECONDS) < 1) {

            // Diff is in weeks
            if ((dateDiff / Constants.WEEK_IN_SECONDS) == 1) {

                dateString = c.getString(R.string.info_time_week).replace(
                        "{weeks}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_week_p).replace(
                        "{weeks}",
                        String.valueOf(dateDiff / Constants.WEEK_IN_SECONDS));

            }

        } else {

            // Diff is probably in years
            if ((dateDiff / Constants.YEAR_IN_SECONDS) == 1) {

                dateString = c.getString(R.string.info_time_year).replace(
                        "{years}", String.valueOf(1));

            } else {

                dateString = c.getString(R.string.info_time_year_p).replace(
                        "{years}",
                        String.valueOf(dateDiff / Constants.YEAR_IN_SECONDS));

            }

        }

        return dateString;

    }

    /**
     * <p>
     * Get the "relative" date
     * </p>
     *
     * @param d the first String, must not be null
     * @param s the second String, must not be null
     * @return String the relative date
     */
    public static final String getRelativeDate(Context c, long d, int s) {

        return c.getString(s).replace("{date}", getRelativeDate(c, d));

    }

    /**
     * <p/>
     * Normalize the given url (adding the http-prefix if none given)
     *
     * @param s the link to be normalized
     * @return link the normalized link
     */

    public static final String normalizeUrl(final String s) {

        // Check if we have a valid prefix
        if ("".equals(s)) {

            return "";

        } else if (s.contains("://")) {

            return s;

        } else {

            return "http://" + s;

        }

    }

    /**
     * <p>
     * Find the Levenshtein distance between two Strings.
     * </p>
     * <p>
     * Credit: <a
     * href="http://www.merriampark.com/ldjava.htm">http://www.merriampark
     * .com/ldjava.htm</a>
     * </p>
     *
     * @param s the first String, must not be null
     * @param t the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input <code>null</code>
     */

    public static int getLevenshteinDistance(String s, String t) {

        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length();
        int m = t.length();

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {

            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();

        }

        int p[] = new int[n + 1];
        int d[] = new int[n + 1];
        int _d[];

        int i;
        int j;

        char t_j;
        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }
        for (j = 1; j <= m; j++) {

            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {

                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
                        + cost);

            }

            _d = p;
            p = d;
            d = _d;
        }

        return p[n];

    }

    public static String timeToLiteral(long s) {

        // Let's see what we can do
        if ((s / 60) < 1) {
            return s + "S";
        } else if ((s / 3600) < 1) {
            return (s / 60) + "M " + (s % 60) + "S";
        } else {
            return (s / 3600) + "H " + ((s % 3600) / 60) + "M";
        }
    }

    /*
     * Author:
     * http://www.mobile-web-consulting.de/post/5272654457/android-check-
     * if-a-service-is-running Modified by: Karl Lindmark
     * @param Context The context to be called from
     * @return boolean True/false regarding if the app is running
     */

    public static boolean isMyServiceRunning(Context context) {

        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {

            if ("com.ninetwozero.battlelog.service.BattlelogService"
                    .equals(service.service.getClassName())) {
                return true;
            }

        }
        return false;

    }

    /*
     * Author: http://stackoverflow.com/a/4239019/860212 Modified by: Karl
     * Lindmark
     * @param Context The context to be called from
     * @return boolean True/false regarding if the network is available
     */

    public static boolean isNetworkAvailable(final Context c) {

        // Let's get the connection manager
        ConnectivityManager connMan = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan.getActiveNetworkInfo() != null) {

            // ...and the network information
            NetworkInfo networkInfo = connMan.getActiveNetworkInfo();

            // If it's WiFi, it's ok
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return true;

            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    && connMan.getBackgroundDataSetting()) {

                return true;

            }

        }
        return false;

    }

    /*
     * Author: Karl Lindmark
     * @param Context The context to be called from
     * @return String The path to the cache directory
     */

    public static String getCachePath(final Context c) {

        // Get the file
        String path = ExternalCacheDirectory.getInstance(c)
                .getExternalCacheDirectory().toString();

        // Append if needed
        if (!path.endsWith("/")) {
            path += "/";
        }

        // Return it
        return path;

    }

    /*
     * Author: Karl Lindmark
     * @param Context The context to be called from
     * @param Bundle The bundle from onCreate()
     * @return Nothing
     */

    public static void restoreCookies(Context context, Bundle icicle) {

        // Did it get passed on?
        if (icicle != null && icicle.containsKey(Constants.SUPER_COOKIES)) {

            List<ShareableCookie> shareableCookies = icicle
                    .getParcelableArrayList(Constants.SUPER_COOKIES);

            if (shareableCookies == null) {

                ((Activity) context).finish();

            } else {

                RequestHandler.setCookies(shareableCookies);

            }

        }

    }

    /*
     * Author: Karl Lindmark
     * @param Context The context to be called from
     * @param SharedPreferences The SharedPreferences for the app
     * @return Nothing
     */

    public static void setupLocale(Context context, SharedPreferences sharedPreferences) {

        if (!sharedPreferences.getString(Constants.SP_BL_LANG, "").equals("")) {

            Locale locale = new Locale(sharedPreferences.getString(
                    Constants.SP_BL_LANG, "en"));
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());

        }

    }

    public static void setupSession(Context context, SharedPreferences sharedPreferences) {

        // Let's just check if it's MainActivity, to prevent loops
        if (context instanceof MainActivity) {

            return;

        }

        // Let's set "active" against the website
        new AsyncSessionSetActive().execute();

        // If we don't have a profile...
        if (SessionKeeper.getProfileData() == null) {

            // ...but we do indeed have a cookie...
            String cookieValue = sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, "");
            if ("".equals(cookieValue)) {

                // ...we set the SessionKeeper, but also reload the cookies!
                // Easy peasy!
                SessionKeeper
                        .setProfileData(SessionKeeper
                                .generateProfileDataFromSharedPreferences(sharedPreferences));
                RequestHandler.setCookies(

                        new ShareableCookie(

                                sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                                cookieValue,
                                Constants.COOKIE_DOMAIN

                        )

                );

                SessionKeeper.setPlatoonData(SessionKeeper
                        .generatePlatoonDataFromSharedPreferences(sharedPreferences));

                // ...but just to be sure, we try to verify our session
                // "behind the scenes"
                new AsyncSessionValidate(context, sharedPreferences).execute();

            } else {

                // Aw man, that backfired.
                Toast.makeText(context, R.string.info_txt_session_lost,
                        Toast.LENGTH_SHORT).show();
                ((Activity) context).startActivity(new Intent(context, MainActivity.class));
                ((Activity) context).finish();

            }

        }

    }

    public static void setupFullscreen(Context context, SharedPreferences sharedPreferences) {

        // Is fullscreen enableD?
        if (sharedPreferences.getBoolean(Constants.SP_BL_FULLSCREEN, true)) {

            ((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE);

        }

    }

    public static String insertToString(String base, Object... data) {

        // Iterate and fix
        for (Object d : data) {

            base = base.replaceFirst("\\{[^\\}]+\\}", String.valueOf(d));

        }
        return base;

    }

    public static String createStringWithData(Context c, int resource, Object... data) {

        // Get the base
        String base = c.getString(resource);

        // Iterate and fix
        for (Object d : data) {

            base = base.replaceFirst("\\{[^\\}]+\\}", String.valueOf(d));

        }
        return base;

    }

}
