package com.ninetwozero.battlelog.jsonmodel.personas;

import com.google.gson.annotations.SerializedName;

public class Persona {

    @SerializedName("picture")
    private String picture;
    @SerializedName("personaId")
    private long personaId;
    @SerializedName("personaName")
    private String personaName;
    @SerializedName("namespace")
    private String platform;
    @SerializedName("clanTag")
    private String clanTag;
    @SerializedName("userId")
    private long userId;

    public Persona(String picture, long personaId, String personaName, String platform, String clanTag, long userId) {
        this.picture = picture;
        this.personaId = personaId;
        this.personaName = personaName;
        this.platform = platform;
        this.clanTag = clanTag;
        this.userId = userId;
    }

    public String getPicture() {
        return picture;
    }

    public long getPersonaId() {
        return personaId;
    }

    public String getPersonaName() {
        return personaName;
    }

    public String getPlatform() {
        return platform;
    }

    public String getClanTag() {
        return clanTag;
    }

    public long getUserId() {
        return userId;
    }
}
