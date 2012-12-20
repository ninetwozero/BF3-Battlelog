package com.ninetwozero.bf3droid.datatype;

public class SimplePlatoon {

    private final String name;
    private final long platoonId;
    private final String platoonBadge;

    public SimplePlatoon(String name, long platoonId, String platoonBadge){
        this.name = name;
        this.platoonId = platoonId;
        this.platoonBadge = platoonBadge;
    }

    public String getName() {
        return name;
    }

    public long getPlatoonId() {
        return platoonId;
    }

    public String getPlatoonBadge() {
        return platoonBadge;
    }
}
