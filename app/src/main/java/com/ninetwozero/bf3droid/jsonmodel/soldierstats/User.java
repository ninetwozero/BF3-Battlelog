package com.ninetwozero.bf3droid.jsonmodel.soldierstats;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("gravatarMd5")
    private String gravatarMd5;
    @SerializedName("userId")
    private long id;
    @SerializedName("username")
    private String userName;

    public User(long createdAt, String gravatarMd5, long id, String userName) {
        this.createdAt = createdAt;
        this.gravatarMd5 = gravatarMd5;
        this.id = id;
        this.userName = userName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getGravatarMd5() {
        return gravatarMd5;
    }

    public void setGravatarMd5(String gravatarMd5) {
        this.gravatarMd5 = gravatarMd5;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
