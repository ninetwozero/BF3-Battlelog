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

import android.util.Log;

public class PublicUtils {

	/**
	 * <p>Get the "relative" Date-string</p>
	 * 
	 * @param date	the Date to be formatted in the {X} {unit}
	 * @return String
	 */
	
	public static String getRelativeDate(final Long d) {
		
		//Let's just expect it to be millis seconds already
		Long dateStart = d;
		Long dateNow = System.currentTimeMillis()/1000;
		Long dateDiff = dateNow - dateStart;
		String dateString = null;
		
		//When did we start? 0 == just one login (if I haven't understood it too wrong)
		if( d == 0 ) { return "an unknown amount of time ago."; }
		
		//What's the difference (in seconds) y'all?
		if( (dateDiff / Constants.minuteInSeconds) < 1 ) {
			
			//Diff is in seconds
			if( dateDiff == 1 ) {
			
				dateString = "{seconds} second ago".replace( "{seconds}", String.valueOf(1) );
				
				
			} else {
				
				dateString = "{seconds} seconds ago".replace( "{seconds}", String.valueOf(dateDiff % Constants.minuteInSeconds) );
			
			}
			
		} else if( (dateDiff / Constants.hourInSeconds) < 1 ) {
			
			//Diff is in minutes
			if( (dateDiff / Constants.minuteInSeconds) == 1 ) {
				
				dateString = "{minutes} minute ago".replace( "{minutes}", String.valueOf(1) );
			
			} else {
				
				dateString = "{minutes} minutes ago".replace( "{minutes}", String.valueOf(dateDiff / Constants.minuteInSeconds) );
				
			}
		} else if( (dateDiff / Constants.dayInSeconds) < 1 ) {
			
			//Diff is in hours
			if( (dateDiff / Constants.hourInSeconds) == 1 ) {
				
				dateString = "{hours} hour ago".replace( "{hours}", String.valueOf(1) );
			
			} else {
			
				dateString = "{hours} hours ago".replace( "{hours}", String.valueOf(dateDiff / Constants.hourInSeconds) );	
				
			}

		} else if( (dateDiff / Constants.weekInSeconds) < 1 ) {
		
			//Diff is in days
			if( (dateDiff / Constants.dayInSeconds) == 1 ) {
			
				dateString = "{days} day ago".replace( "{days}", String.valueOf(1) );
				
			} else {
				
				dateString = "{days} days ago".replace( "{days}", String.valueOf(dateDiff / Constants.dayInSeconds) );
			
			}
				
		} else if( (dateDiff / Constants.yearInSeconds) < 1 ) {
			
			//Diff is in weeks
			if( (dateDiff / Constants.weekInSeconds) == 1 ) {
			
				dateString = "{weeks} week ago".replace( "{weeks}", String.valueOf(1) );
				
			} else {
				
				dateString = "{weeks} weeks ago".replace( "{weeks}", String.valueOf(dateDiff / Constants.weekInSeconds) );
			
			}	
			
		} else {
		
			//Diff is probably in years
			if( (dateDiff / Constants.yearInSeconds) == 1 ) {
			
				dateString = "{years} year ago".replace( "{years}", String.valueOf(1) );
				
			} else {
				
				dateString = "{years} years ago".replace( "{years}", String.valueOf(dateDiff / Constants.yearInSeconds ) );
		
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
	public static final String getRelativeDate(long d, String s) {
		
		return s + " " + getRelativeDate(d);
		
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

}