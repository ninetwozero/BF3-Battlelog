
package com.ninetwozero.battlelog.datatype;

public class PlatformData {

    // Attributes
    private int id;
    private String name;

    // Constructs
    public PlatformData(int pId, String pName) {

        this.id = pId;
        this.name = pName;

    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

}
