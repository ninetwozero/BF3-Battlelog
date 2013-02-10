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

package com.ninetwozero.bf3droid.misc;

import android.app.Activity;
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
import com.ninetwozero.bf3droid.MainActivity;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.asynctask.AsyncSessionSetActive;
import com.ninetwozero.bf3droid.asynctask.AsyncSessionValidate;
import com.ninetwozero.bf3droid.datatype.ShareableCookie;
import com.ninetwozero.bf3droid.http.RequestHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PublicUtils {

    public static String getDate(final Long d) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(d * 1000));
    }

    public static final String getDate(long d, String s) {
        return s + " " + getDate(d);
    }

    public static String getRelativeDate(final Context c, final long d) {
        long dateStart = d;
        long dateNow = System.currentTimeMillis() / 1000;
        long dateDiff = dateNow - dateStart;
        String dateString;

        if (d == 0) {
            return "N/A";
        }

        dateDiff = (dateDiff < 0) ? 0 : dateDiff;
        if ((dateDiff / Constants.MINUTE_IN_SECONDS) < 1) {
            // Diff is in the scope of seconds
            if (dateDiff == 1) {
                dateString = c.getString(R.string.info_time_second).replace("{seconds}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_second_p).replace(
                        "{seconds}",
                        String.valueOf(dateDiff % Constants.MINUTE_IN_SECONDS)
                );
            }
        } else if ((dateDiff / Constants.HOUR_IN_SECONDS) < 1) {
            // Diff is in the scope of minutes
            if ((dateDiff / Constants.MINUTE_IN_SECONDS) == 1) {
                dateString = c.getString(R.string.info_time_min).replace("{minutes}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_min_p).replace(
                        "{minutes}",
                        String.valueOf(dateDiff / Constants.MINUTE_IN_SECONDS)
                );
            }
        } else if ((dateDiff / Constants.DAY_IN_SECONDS) < 1) {
            // Diff is in the scope of hours
            if ((dateDiff / Constants.HOUR_IN_SECONDS) == 1) {
                dateString = c.getString(R.string.info_time_hour).replace("{hours}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_hour_p).replace(
                        "{hours}",
                        String.valueOf(dateDiff / Constants.HOUR_IN_SECONDS)
                );
            }
        } else if ((dateDiff / Constants.WEEK_IN_SECONDS) < 1) {
            // Diff is in the scope of days
            if ((dateDiff / Constants.DAY_IN_SECONDS) == 1) {
                dateString = c.getString(R.string.info_time_day).replace("{days}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_day_p).replace(
                        "{days}",
                        String.valueOf(dateDiff / Constants.DAY_IN_SECONDS)
                );
            }
        } else if ((dateDiff / Constants.YEAR_IN_SECONDS) < 1) {
            // Diff is in the scope of weeks
            if ((dateDiff / Constants.WEEK_IN_SECONDS) == 1) {
                dateString = c.getString(R.string.info_time_week).replace("{weeks}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_week_p).replace(
                        "{weeks}",
                        String.valueOf(dateDiff / Constants.WEEK_IN_SECONDS)
                );
            }
        } else {
            // Diff is most likely in the scope of years(++)
            if ((dateDiff / Constants.YEAR_IN_SECONDS) == 1) {
                dateString = c.getString(R.string.info_time_year).replace("{years}", String.valueOf(1));
            } else {
                dateString = c.getString(R.string.info_time_year_p).replace(
                        "{years}",
                        String.valueOf(dateDiff / Constants.YEAR_IN_SECONDS)
                );
            }
        }
        return dateString;
    }

    public static final String getRelativeDate(Context c, long d, int s) {
        return c.getString(s).replace("{date}", getRelativeDate(c, d));
    }

    public static final String normalizeUrl(final String s) {
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
        if ((s / 60) < 1) {
            return s + "S";
        } else if ((s / 3600) < 1) {
            return (s / 60) + "M " + (s % 60) + "S";
        } else {
            return (s / 3600) + "H " + ((s % 3600) / 60) + "M";
        }
    }

    public static boolean isNetworkAvailable(final Context c) {
        ConnectivityManager connMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan.getActiveNetworkInfo() != null) {
            NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && connMan.getBackgroundDataSetting()) {
                return true;
            }
        }
        return false;
    }

    public static String getCachePath(final Context c) {
        String path = ExternalCacheDirectory.getInstance(c).getExternalCacheDirectory().toString();
        if (!path.endsWith("/")) {
            path += "/";
        }
        return path;
    }

    public static void restoreCookies(Context context, Bundle icicle) {
        if (icicle != null && icicle.containsKey(Constants.SUPER_COOKIES)) {
            List<ShareableCookie> shareableCookies = icicle.getParcelableArrayList(Constants.SUPER_COOKIES);
            if (shareableCookies == null) {
                ((Activity) context).finish();
            } else {
                RequestHandler.setCookies(shareableCookies);
            }
        }
    }

    public static void setupLocale(Context context, SharedPreferences sharedPreferences) {
        if (!sharedPreferences.getString(Constants.SP_BL_LANG, "").equals("")) {
            Locale locale = new Locale(sharedPreferences.getString(Constants.SP_BL_LANG, "en"));
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    public static void setupSession(Context context, SharedPreferences sharedPreferences) {
        if (context instanceof MainActivity) {
            return;
        }

        new AsyncSessionSetActive().execute();
        if (SessionKeeper.getProfileData() == null) {
            String cookieValue = sharedPreferences.getString(Constants.SP_BL_COOKIE_VALUE, "");
            if ("".equals(cookieValue)) {
                SessionKeeper.setProfileData(
                        SessionKeeper.generateProfileDataFromSharedPreferences(sharedPreferences)
                );
                /*RequestHandler.setCookies(
                        new ShareableCookie(
                                sharedPreferences.getString(Constants.SP_BL_COOKIE_NAME, ""),
                                cookieValue,
                                Constants.COOKIE_DOMAIN
                        )
                );*/
                SessionKeeper.setPlatoonData(SessionKeeper.generatePlatoonDataFromSharedPreferences(sharedPreferences));
                new AsyncSessionValidate(context, sharedPreferences).execute();
            } else {
                Toast.makeText(context, R.string.info_txt_session_lost, Toast.LENGTH_SHORT).show();
                ((Activity) context).startActivity(new Intent(context, MainActivity.class));
                ((Activity) context).finish();
            }
        }
    }

    public static void setupFullscreen(Context context, SharedPreferences sharedPreferences) {
        if (sharedPreferences.getBoolean(Constants.SP_BL_FULLSCREEN, true)) {
            ((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public static String insertToString(String base, Object... data) {
        for (Object d : data) {
            base = base.replaceFirst("\\{[^\\}]+\\}", String.valueOf(d));
        }
        return base;
    }

    public static String createStringWithData(Context c, int resource, Object... data) {
        return insertToString(c.getString(resource), data);
    }
}
