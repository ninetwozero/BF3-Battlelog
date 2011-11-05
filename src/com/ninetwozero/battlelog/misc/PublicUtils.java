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

public class PublicUtils {


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
