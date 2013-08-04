package com.ninetwozero.bf3droid.datatype;

import com.ninetwozero.bf3droid.util.Platform;

public class SimplePlatoon {

    private final String name;
    private final long platoonId;
    private final String platoonBadge;
    private final String platform;
    private final String membersCount;

    public SimplePlatoon(String name, long platoonId) {
        this(name, platoonId, "", "", "");
    }

    public SimplePlatoon(String name, long platoonId, String platoonBadge, String platform, String membersCount) {
        this.name = name;
        this.platoonId = platoonId;
        this.platoonBadge = platoonBadge;
        this.platform = platform;
        this.membersCount = membersCount;
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

    public String getMembersCount() {
        return membersCount;
    }

    public int platformId() {
        return Platform.resolveIdFromPlatformName(platform);
    }
}
