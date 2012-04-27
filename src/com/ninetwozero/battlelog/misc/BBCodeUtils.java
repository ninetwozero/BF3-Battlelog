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

import com.ninetwozero.battlelog.R;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBCodeUtils {

    /*
     * Author: Karl Lindmark
     * @param String The content to be bbcoded
     * @return String The bbcoded content
     */

    public static String toBBCode(final String originalContent,
            final HashMap<Long, String> quotes) {

        // Let's start off
        String convertedContent = originalContent;

        // ArrayList
        String stringMatchesPre = null, stringMatchesPost = null;

        // Build compile the patterns
        Pattern patternLink = Pattern
                .compile(Constants.PATTERN_POST_FORUM_LINK);
        // Pattern patternLink = Pattern.compile(
        // "<a href=\"([^\\\"]+)\" rel=\"nofollow\">([^\\<]+)<\\/a> \\[([^\\]]+)\\]"
        // );
        Pattern patternQuote = Pattern.compile("@q:([0-9]+):([^@]+)@");
        Pattern patternBold = Pattern.compile("\\*\\*([^\\*]+)\\*\\*");
        Pattern patternUnderline = Pattern.compile("__([^\\_]+)__");
        Pattern patternStrike = Pattern.compile("--([^\\-]+)--");
        Pattern patternItalic = Pattern.compile("_-([^\\\"_\\-\\\"]+)-_");

        // Iterate over the findings
        Matcher matcherQuote = patternQuote.matcher(convertedContent);
        while (matcherQuote.find()) {

            stringMatchesPre = matcherQuote.group();
            stringMatchesPost = Constants.BBCODE_TAG_QUOTE_OUT.replace(

                    "{username}", matcherQuote.group(2)

                    ).replace(

                            "{text}", quotes.get(Long.parseLong(matcherQuote.group(1)))

                    );

            // Do the actual replacement
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherLink = patternLink.matcher(convertedContent);
        while (matcherLink.find()) {

            stringMatchesPre = matcherLink.group();
            convertedContent = convertedContent.replace(stringMatchesPre,
                    matcherLink.group(1));

        }

        Matcher matcherItalic = patternItalic.matcher(convertedContent);
        while (matcherItalic.find()) {

            stringMatchesPre = matcherItalic.group();
            stringMatchesPost = Constants.BBCODE_TAG_ITALIC_OUT.replace(
                    "{text}", matcherItalic.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherBold = patternBold.matcher(convertedContent);
        while (matcherBold.find()) {

            stringMatchesPre = matcherBold.group();
            stringMatchesPost = Constants.BBCODE_TAG_BOLD_OUT.replace("{text}",
                    matcherBold.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherStrike = patternStrike.matcher(convertedContent);
        while (matcherStrike.find()) {

            stringMatchesPre = matcherStrike.group();
            stringMatchesPost = Constants.BBCODE_TAG_STRIKE_OUT.replace(
                    "{text}", matcherStrike.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherUnderline = patternUnderline.matcher(convertedContent);
        while (matcherUnderline.find()) {

            stringMatchesPre = matcherUnderline.group();
            stringMatchesPost = Constants.BBCODE_TAG_UNDERLINE_OUT.replace(
                    "{text}", matcherUnderline.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        // Return it back
        return convertedContent.replace("<br/>", "\n");

    }

}
