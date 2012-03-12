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

package com.ninetwozero.battlelog.datatypes;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public interface DefaultFragment {
    
    public void initFragment(View v);
    public void reload();    
    public Menu prepareOptionsMenu(Menu menu);
    public boolean handleSelectedOption(MenuItem item);

}
