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

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import com.ninetwozero.battlelog.R;


public class PublicUtils {

	/**
	 * <p>Get the Date-string (YYYY-MM-DD</p>
	 * 
	 * @param date	the Date to be formatted in the {X} {unit}
	 * @return String
	 */
	
	public static String getDate(final Long d) {
		
		return new SimpleDateFormat("yyyy-MM-dd").format( new Date(d*1000) );
		
	}
	
	/**
	* <p>Get the "relative" date</p>
	*
	* @param d  the first String, must not be null
	* @param s  the second String, must not be null
	* @return String the relative date
	*/
	
	public static final String getDate(long d, String s) {
		
		return s + " " + getDate(d);
		
	}
		
	/**
	 * <p>Get the "relative" Date-string</p>
	 * 
	 * @param date	the Date to be formatted in the {X} {unit}
	 * @return String
	 */
	
	public static String getRelativeDate(final Context c, final Long d) {
		
		//Let's just expect it to be millis seconds already
		Long dateStart = d;
		Long dateNow = System.currentTimeMillis()/1000;
		Long dateDiff = dateNow - dateStart;
		String dateString = null;
		
		//When did we start? 0 == just one login (if I haven't understood it too wrong)
		if( d == 0 ) { return "an unknown amount of time ago."; }
		
		//Diff is not allowed to be < 0
		dateDiff = ( dateDiff < 0 )? 0 : dateDiff;
				
		//What's the difference (in seconds) y'all?
		if( (dateDiff / Constants.MINUTE_IN_SECONDS) < 1 ) {
			
			//Diff is in seconds
			if( dateDiff == 1 ) {
			
				dateString = c.getString( R.string.info_time_second ).replace( "{seconds}", String.valueOf(1) );
				
				
			} else {
				
				dateString = c.getString( R.string.info_time_second_p ).replace( "{seconds}", String.valueOf(dateDiff % Constants.MINUTE_IN_SECONDS) );
			
			}
			
		} else if( (dateDiff / Constants.HOUR_IN_SECONDS) < 1 ) {
			
			//Diff is in minutes
			if( (dateDiff / Constants.MINUTE_IN_SECONDS) == 1 ) {
				
				dateString = c.getString( R.string.info_time_min ).replace( "{minutes}", String.valueOf(1) );
			
			} else {
				
				dateString = c.getString( R.string.info_time_min_p ).replace( "{minutes}", String.valueOf(dateDiff / Constants.MINUTE_IN_SECONDS) );
				
			}
		} else if( (dateDiff / Constants.DAY_IN_SECONDS) < 1 ) {
			
			//Diff is in hours
			if( (dateDiff / Constants.HOUR_IN_SECONDS) == 1 ) {
				
				dateString = c.getString( R.string.info_time_hour ).replace( "{hours}", String.valueOf(1) );
			
			} else {
			
				dateString = c.getString( R.string.info_time_hour_p ).replace( "{hours}", String.valueOf(dateDiff / Constants.HOUR_IN_SECONDS) );	
				
			}

		} else if( (dateDiff / Constants.WEEK_IN_SECONDS) < 1 ) {
		
			//Diff is in days
			if( (dateDiff / Constants.DAY_IN_SECONDS) == 1 ) {
			
				dateString = c.getString( R.string.info_time_day ).replace( "{days}", String.valueOf(1) );
				
			} else {
				
				dateString = c.getString( R.string.info_time_day_p ).replace( "{days}", String.valueOf(dateDiff / Constants.DAY_IN_SECONDS) );
			
			}
				
		} else if( (dateDiff / Constants.YEAR_IN_SECONDS) < 1 ) {
			
			//Diff is in weeks
			if( (dateDiff / Constants.WEEK_IN_SECONDS) == 1 ) {
			
				dateString = c.getString( R.string.info_time_week ).replace( "{weeks}", String.valueOf(1) );
				
			} else {
				
				dateString = c.getString( R.string.info_time_week_p ).replace( "{weeks}", String.valueOf(dateDiff / Constants.WEEK_IN_SECONDS) );
			
			}	
			
		} else {
		
			//Diff is probably in years
			if( (dateDiff / Constants.YEAR_IN_SECONDS) == 1 ) {
			
				dateString = c.getString( R.string.info_time_year ).replace( "{years}", String.valueOf(1) );
				
			} else {
				
				dateString = c.getString( R.string.info_time_year_p ).replace( "{years}", String.valueOf(dateDiff / Constants.YEAR_IN_SECONDS ) );
		
			}
		
		}
		
		return dateString;
		
	}

  /**
   * <p>Get the "relative" date</p>
   *
   * @param d  the first String, must not be null
   * @param s  the second String, must not be null
   * @return String the relative date
   */
	public static final String getRelativeDate(Context c, long d, int s) {
		
		return c.getResources().getString( s ).replace( "{date}", getRelativeDate(c, d) );
		
	}
	
	
	/**
	 *  <p>Normalize the given url (adding the http-prefix if none given)
	 *  
	 *  @param s the link to be normalized
	 *  @return link the normalized link
	 * 
	 */
	
	public final static String normalizeUrl( final String s ) {
		
		//Check if we have a valid prefix
		if( s.equals( "" ) ) {
		
			return "";
			
		} else if( s.contains( "://" ) ) { 
			
			return s;
		
		} else {
			
			return "http://" + s;
		
		}
		
	}
  /**
   * <p>Find the Levenshtein distance between two Strings.</p>
   * <p>Credit: <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a> </p>
   *
   * @param s  the first String, must not be null
   * @param t  the second String, must not be null
   * @return result distance
   * @throws IllegalArgumentException if either String input <code>null</code>
   */

	public static int getLevenshteinDistance(String s, String t) {
      
		if (s == null || t == null) { throw new IllegalArgumentException("Strings must not be null"); }

		int n = s.length();
		int m = t.length();

		if (n == 0) return m;
		else if (m == 0) return n;

		if (n > m) {

			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
      
		}

		int p[] = new int[n+1];
		int d[] = new int[n+1];
		int _d[];

		int i;
		int j;

		char t_j;
		int cost;

		for (i = 0; i <= n; i++) { p[i] = i; }
		for (j = 1; j <= m; j++) {
    	  
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
          
				cost = s.charAt(i-1) == t_j ? 0 : 1;
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
          
			}

			_d = p;
			p = d;
			d = _d;
		}
      
		return p[n];
	
	}
	
	public static String timeToLiteral(long s) {
	    
    	//Let's see what we can do
    	if( ( s / 60) < 1 ) return s + "S" ;
    	else if( (s / 3600 ) < 1 ) return (s/60) + "M " + (s % 60) + "S" ;
    	else return (s/3600) + "H " + ((s % 3600)/60) + "M";		
    	
    }
	
	/*
	 * 	Author: http://www.mobile-web-consulting.de/post/5272654457/android-check-if-a-service-is-running
	 *  Modified by: Karl Lindmark
	 *  
	 *  @param Context The context to be called from
	 *  @return boolean True/false regarding if the app is running
	 */
	
    public static boolean isMyServiceRunning(Context context) {
    	
    	ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        
    		if( "com.ninetwozero.battlelog.services.BattlelogService".equals( service.service.getClassName() ) ) {
                return true;
            }
        
    	}
        return false;
    
    }


}