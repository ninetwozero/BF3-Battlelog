package com.ninetwozero.bf3droid.model;

import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static final String USER = "user";
    public static final String GUEST = "guest";

    private final String name;
    private final long id;
    private List<SimplePersona> personas;
    private List<SimplePlatoon> platoons;
    private long selectedPersona;
    private long selectedPlatoon;

    public User(String name, long id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public List<SimplePersona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<SimplePersona> personas){
        this.personas = new ArrayList<SimplePersona>(personas);
    }

    public void selectPersona(long selectedPersona) {
        this.selectedPersona = selectedPersona;
    }

    public SimplePersona selectedPersona() {
        if (selectedPersona == 0) {
            return personas.get(0);
        }
        for (SimplePersona simplePersona : personas) {
            if (simplePersona.getPersonaId() == selectedPersona) {
                return simplePersona;
            }
        }
        return personas.get(0);
    }

    public void setPlatoons(List<SimplePlatoon> platoons) {
        this.platoons = platoons;
    }

    public List<SimplePlatoon> getPlatoons() {
        return platoons;
    }

    public void selectPlatoon(long selectedPlatoon) {
        this.selectedPlatoon = selectedPlatoon;
    }

    public SimplePlatoon selectedPlatoon() {
        if (selectedPlatoon == 0) {
            return platoons.get(0);
        }
        for (SimplePlatoon simplePlatoon : platoons) {
            if (simplePlatoon.getPlatoonId() == selectedPlatoon) {
                return simplePlatoon;
            }
        }
        return platoons.get(0);
    }
}
