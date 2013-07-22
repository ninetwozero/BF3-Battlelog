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

public class ResolvePlatform {
    public static String platformName(int id) {
        switch (id) {
            case 0:
            case 1:
                return "[PC]";
            case 2:
                return "[360]";
            case 4:
                return "[PS3]";
            default:
                return "[N/A]";
        }
    }
}
