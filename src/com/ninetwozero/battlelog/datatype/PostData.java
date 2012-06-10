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

public class PostData {

    // Attributes
    private String field, value;
    private boolean hash;

    // Constructs
    public PostData() {

        this.field = "";
        this.value = "";
        this.hash = false;
    }

    public PostData(String f, Object v) {

        this.field = f;
        this.value = String.valueOf(v);
        this.hash = false;
    }

    public PostData(String f, Object v, boolean h) {

        this.field = f;
        this.value = String.valueOf(v);
        this.hash = h;
    }

    // Other
    public void clear() {

        this.field = "";
        this.value = "";
        this.hash = false;
    }

    public String find(String f) {

        return (this.field.compareTo(f) == 1) ? this.field : null;
    }

    // Getters
    public String getField() {

        return field;
    }

    public String getValue() {

        return value;
    }

    public boolean isHash() {

        return hash;
    }

    public void setAll(String f, Object v, boolean h) {

        this.field = f;
        this.value = String.valueOf(v);
        this.hash = h;
    }

    // Setters
    public void setField(String field) {

        this.field = field;
    }

    public void setHash(boolean hash) {

        this.hash = hash;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public String toString() {

        return ((this.isHash()) ? "[HASH] " : "[PLAIN] ") + this.field + " => "
                + this.value;
    }
}
