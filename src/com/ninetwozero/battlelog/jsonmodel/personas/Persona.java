package com.ninetwozero.battlelog.jsonmodel.personas;

import com.google.gson.annotations.SerializedName;

public class Persona {

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

    public Persona(long personaId, String personaName, String platform, String clanTag, long userId) {
        this.personaId = personaId;
        this.personaName = personaName;
        this.platform = platform;
        this.clanTag = clanTag;
        this.userId = userId;
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
