package com.ninetwozero.battlelog.model;

public class SelectedPersona {
    private long personaId;
    private String personaName;

    public SelectedPersona(long personaId, String personaName) {
        this.personaId = personaId;
        this.personaName = personaName;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }
}
