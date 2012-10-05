package com.ninetwozero.battlelog.jsonmodel.soldierstats;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("createdAt")
    private long createdAt;
    @SerializedName("gravatarMd5")
    private String md5;
    @SerializedName("userId")
    private long id;
    @SerializedName("username")
    private String userName;

    public User(long createdAt, String md5, long id, String userName) {
        this.createdAt = createdAt;
        this.md5 = md5;
        this.id = id;
        this.userName = userName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
