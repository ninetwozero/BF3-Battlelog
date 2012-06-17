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

import java.util.List;

public class WeaponVehicleDataWrapper {

    // Attributes
    private List<WeaponVehicleListData> weapons;
    private List<WeaponVehicleListData> vehicles;

    // Construct
    public WeaponVehicleDataWrapper(List<WeaponVehicleListData> w, List<WeaponVehicleListData> v) {

        this.weapons = w;
        this.vehicles = v;

    }

    // Getters
    public List<WeaponVehicleListData> getWeapons() {
        return this.weapons;
    }

    public List<WeaponVehicleListData> getVehicles() {
        return this.vehicles;
    }

}
