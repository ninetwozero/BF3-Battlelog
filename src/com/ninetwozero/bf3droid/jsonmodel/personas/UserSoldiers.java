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

package com.ninetwozero.bf3droid.jsonmodel.personas;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UserSoldiers {

    @SerializedName("soldiersBox")
    private List<Soldier> soldiers;
    
    public UserSoldiers(List<Soldier> soldier) {
    	soldiers = soldier;
    }

    public List<Soldier> getSoldierInfo() {
        return soldiers;
    }
}
