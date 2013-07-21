package com.ninetwozero.bf3droid.jsonmodel.platoon;

import com.google.gson.annotations.SerializedName;
import com.ninetwozero.bf3droid.jsonmodel.personas.Persona;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.User;

public class PlatoonMember {
    @SerializedName("platoonId")
    private long platoonId;
    @SerializedName("persona")
    private Persona persona;
    @SerializedName("user")
    private User user;
    @SerializedName("personaId")
    private long personaId;
    @SerializedName("id")
    private long id;
    @SerializedName("membershipLevel")
    private int membershipLevel;

    public PlatoonMember(long platoonId, Persona persona, User user, long personaId, long id, int membershipLevel) {
        this.platoonId = platoonId;
        this.persona = persona;
        this.user = user;
        this.personaId = personaId;
        this.id = id;
        this.membershipLevel = membershipLevel;
    }

    public long getPlatoonId() {
        return platoonId;
    }

    public Persona getPersona() {
        return persona;
    }

    public User getUser() {
        return user;
    }

    public long getPersonaId() {
        return personaId;
    }

    public long getId() {
        return id;
    }

    public int getMembershipLevel() {
        return membershipLevel;
    }
}
