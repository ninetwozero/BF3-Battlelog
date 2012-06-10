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

package com.ninetwozero.battlelog.datatype;

public class WeaponVehicleListData {

    // Attributes
    private String title, slug, image;

    public WeaponVehicleListData(String t, String s, String i) {

        title = t;
        slug = s;
        image = i;

    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getImage() {
        return image;
    }

}
