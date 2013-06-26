package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.bf3droid.util.DateTimeFormatter;

public class Platoon {

    @SerializedName("website")
    private String website;
    @SerializedName("memberCounter")
    private int memberCounter;
    @SerializedName("name")
    private String name;
    @SerializedName("platform")
    private int platform;
    @SerializedName("tag")
    private String tag;
    @SerializedName("members")
    private PlatoonMembers members;
    @SerializedName("fanCounter")
    private int fanCounter;
    @SerializedName("creationDate")
    private long creationDate;
    @SerializedName("presentation")
    private String presentation;
    @SerializedName("id")
    private long id;

    public Platoon(String website, int memberCounter, String name, int platform, String tag, PlatoonMembers members, int fanCounter, long creationDate, String presentation, long id) {
        this.website = website;
        this.memberCounter = memberCounter;
        this.name = name;
        this.platform = platform;
        this.tag = tag;
        this.members = members;
        this.fanCounter = fanCounter;
        this.creationDate = creationDate;
        this.presentation = presentation;
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public int getMemberCounter() {
        return memberCounter;
    }

    public String getName() {
        return name;
    }

    public int getPlatform() {
        return platform;
    }

    public String getTag() {
        return tag;
    }

    public PlatoonMembers getMembers() {
        return members;
    }

    public int getFanCounter() {
        return fanCounter;
    }

    public String getCreationDate() {
        return DateTimeFormatter.dateString(creationDate);
    }

    public String getPresentation() {
        return presentation;
    }

    public long getId() {
        return id;
    }
}
