/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/   

package com.ninetwozero.battlelog.misc;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import com.ninetwozero.battlelog.R;

public class BBCodeUtils {
	
	  /*
     * 	Author: Karl Lindmark
     * 
     * 	@param String The content to be bbcoded
     * 	@return String The bbcoded content
     * 
     */

    public static String toBBCode(final String originalContent, final HashMap<Long, String> quotes) {
    
    	//Let's start off
    	String convertedContent = originalContent;

    	//ArrayList
    	String stringMatchesPre = null, stringMatchesPost = null;
    	
    	//Build compile the patterns
    	Pattern patternQuote = Pattern.compile( "@q:([0-9]+):([^@]+)@" );
    	Pattern patternBold = Pattern.compile( "\\*\\*([^\\*]+)\\*\\*" );
    	Pattern patternUnderline = Pattern.compile( "__([^\\_]+)__" );
    	Pattern patternStrike = Pattern.compile( "--([^\\-]+)--" );
    	Pattern patternItalic = Pattern.compile( "_-([^\\\"_\\-\\\"]+)-_" );

    	//Ready the matchers
    	Matcher matcherQuote = patternQuote.matcher( convertedContent );
       	Matcher matcherBold = patternBold.matcher( convertedContent );
    	Matcher matcherUnderline = patternUnderline.matcher( convertedContent );
    	Matcher matcherStrike = patternStrike.matcher( convertedContent );
    	Matcher matcherItalic = patternItalic.matcher( convertedContent );
     	
    	//Iterate over the findings
    	while( matcherQuote.find() ) {
    		
    		stringMatchesPre = matcherQuote.group();
    		stringMatchesPost = Constants.BBCODE_TAG_QUOTE_OUT.replace( 
    					
				"{username}",
				matcherQuote.group( 2 )
			
			).replace(
					
				"{text}", 
				quotes.get( Long.parseLong( matcherQuote.group( 1 ) ) )
				
    		);
    		
    		//Do the actual replacement
    		convertedContent = convertedContent.replace( stringMatchesPre, stringMatchesPost );
    		
    	}
    	
    	while( matcherItalic.find() ) {
    		
    		stringMatchesPre = matcherItalic.group();
    		stringMatchesPost = Constants.BBCODE_TAG_ITALIC_OUT.replace( "{text}", matcherItalic.group(1) );
    		convertedContent = convertedContent.replace( stringMatchesPre, stringMatchesPost );

    		Log.d(Constants.DEBUG_TAG, stringMatchesPre + " => " + stringMatchesPost );    		
    	}
    	
    	while( matcherBold.find() ) {
        	
    		stringMatchesPre = matcherBold.group();
    		stringMatchesPost = Constants.BBCODE_TAG_BOLD_OUT.replace( "{text}", matcherBold.group(1) );
    		convertedContent = convertedContent.replace( stringMatchesPre, stringMatchesPost );

    		Log.d(Constants.DEBUG_TAG, stringMatchesPre + " => " + stringMatchesPost );    		
    	}
    	
    	while( matcherStrike.find() ) {
    	
    		stringMatchesPre = matcherStrike.group();
    		stringMatchesPost = Constants.BBCODE_TAG_STRIKE_OUT.replace( "{text}", matcherStrike.group(1) );
    		convertedContent = convertedContent.replace( stringMatchesPre, stringMatchesPost );

    		Log.d(Constants.DEBUG_TAG, stringMatchesPre + " => " + stringMatchesPost );    		
    	}
    	
    	while( matcherUnderline.find() ) {
    	
    		stringMatchesPre = matcherUnderline.group();
    		stringMatchesPost = Constants.BBCODE_TAG_UNDERLINE_OUT.replace( "{text}", matcherUnderline.group(1) );
    		convertedContent = convertedContent.replace( stringMatchesPre, stringMatchesPost );

    		Log.d(Constants.DEBUG_TAG, stringMatchesPre + " => " + stringMatchesPost );    		
    	}
   	
    	//Return it back
    	return convertedContent;
    	
    }
    
}
