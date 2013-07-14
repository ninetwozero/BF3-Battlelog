/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.misc;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BBCodeUtils {

    // Constants
    public static final String TAG_BOLD_IN = "**{text}**";
    public static final String TAG_BOLD_OUT = "[b]{text}[/b]";
    public static final String TAG_BB_STRIKE_IN = "--{text}--";
    public static final String TAG_STRIKE_OUT = "[s]{text}[/s]";
    public static final String TAG_UNDERLINE_IN = "__{text}__";
    public static final String TAG_UNDERLINE_OUT = "[u]{text}[/u]";
    public static final String TAG_ITALIC_IN = "_-{text}-_";
    public static final String TAG_ITALIC_OUT = "[i]{text}[/i]";
    public static final String TAG_QUOTE_IN = "@q:{number}:{username}@\n";
    public static final String TAG_QUOTE_OUT = "[quote {username} said:]{text}[/quote]";

    public static final String PATTERN_QUOTE = "@q:([0-9]+):([^@]+)@";
    public static final String PATTERN_BOLD = "\\*\\*([^\\*]+)\\*\\*";
    public static final String PATTERN_UNDERLINE = "__([^\\_]+)__";
    public static final String PATTERN_STRIKE = "--([^\\-]+)--";
    public static final String PATTERN_ITALIC = "_-([^\\\"_\\-\\\"]+)-_";

    /*
     * Author: Karl Lindmark
     * @param String The content to be bbcoded
     * @return String The bbcoded content
     */

    public static String toBBCode(final String originalContent,
                                  final Map<Long, String> quotes) {

        // Let's start off
        String convertedContent = originalContent;

        // ArrayList
        String stringMatchesPre;
        String stringMatchesPost;

        // Build compile the patterns
        Pattern patternLink = Pattern
                .compile(Constants.PATTERN_POST_FORUM_LINK);
        // Pattern patternLink = Pattern.compile(
        // "<a href=\"([^\\\"]+)\" rel=\"nofollow\">([^\\<]+)<\\/a> \\[([^\\]]+)\\]"
        // );
        Pattern patternQuote = Pattern.compile(PATTERN_QUOTE);
        Pattern patternBold = Pattern.compile(PATTERN_BOLD);
        Pattern patternUnderline = Pattern.compile(PATTERN_UNDERLINE);
        Pattern patternStrike = Pattern.compile(PATTERN_STRIKE);
        Pattern patternItalic = Pattern.compile(PATTERN_ITALIC);

        // Iterate over the findings
        Matcher matcherQuote = patternQuote.matcher(convertedContent);
        while (matcherQuote.find()) {

            stringMatchesPre = matcherQuote.group();
            stringMatchesPost = TAG_QUOTE_OUT.replace(

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
            stringMatchesPost = TAG_ITALIC_OUT.replace(
                    "{text}", matcherItalic.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherBold = patternBold.matcher(convertedContent);
        while (matcherBold.find()) {

            stringMatchesPre = matcherBold.group();
            stringMatchesPost = TAG_BOLD_OUT.replace("{text}",
                    matcherBold.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherStrike = patternStrike.matcher(convertedContent);
        while (matcherStrike.find()) {

            stringMatchesPre = matcherStrike.group();
            stringMatchesPost = TAG_STRIKE_OUT.replace(
                    "{text}", matcherStrike.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        Matcher matcherUnderline = patternUnderline.matcher(convertedContent);
        while (matcherUnderline.find()) {

            stringMatchesPre = matcherUnderline.group();
            stringMatchesPost = TAG_UNDERLINE_OUT.replace(
                    "{text}", matcherUnderline.group(1));
            convertedContent = convertedContent.replace(stringMatchesPre,
                    stringMatchesPost);

        }

        // Return it back
        return convertedContent.replace("<br/>", "\n");

    }

}
