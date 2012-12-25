package com.ninetwozero.bf3droid.datatype;

public class SimplePlatoon {

    private final String name;
    private final long platoonId;
    private final String platoonBadge;
    private final String platform;

    public SimplePlatoon(String name, long platoonId, String platoonBadge, String platform){
        this.name = name;
        this.platoonId = platoonId;
        this.platoonBadge = platoonBadge;
        this.platform = platform;
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

    public String getPlatform() {
        return platform;
    }
}
