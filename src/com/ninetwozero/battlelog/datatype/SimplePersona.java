package com.ninetwozero.battlelog.datatype;

import java.net.URL;

public class SimplePersona {

    private final String personaName;
    private final long personaId;
    private final String platform;
    private final String personaImage;

    public SimplePersona(String personaName, long personaId, String platform, String personaImage){
        this.personaName = personaName;
        this.personaId = personaId;
        this.platform = platform;
        this.personaImage = personaImage;
    }

    public String getPersonaName() {
        return personaName;
    }

    public long getPersonaId() {
        return personaId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPersonaImage() {
        return personaImage;
    }
}
