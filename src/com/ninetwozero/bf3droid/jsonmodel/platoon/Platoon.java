package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Date creationDate;
    @SerializedName("presentation")
    private String presentation;
    @SerializedName("id")
    private long id;

    public Platoon(String website, int memberCounter, String name, int platform, String tag, PlatoonMembers members, int fanCounter, int creationDate, String presentation, long id) {
        this.website = website;
        this.memberCounter = memberCounter;
        this.name = name;
        this.platform = platform;
        this.tag = tag;
        this.members = members;
        this.fanCounter = fanCounter;
        setCreationDate(creationDate);
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

    private void setCreationDate(int creationDate) {
        this.creationDate = new Date(creationDate);
    }

    public String getCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        return sdf.format(creationDate);
    }

    public String getPresentation() {
        return presentation;
    }

    public long getId() {
        return id;
    }
}
