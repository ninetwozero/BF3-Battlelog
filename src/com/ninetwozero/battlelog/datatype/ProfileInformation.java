/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.datatype;

import java.util.List;

import android.content.Context;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ProfileInformation {

    // Attributes
    private int age;
    private long userId, dateOfBirth, lastlogin, statusMessageChanged;
    private PersonaData[] persona;
    private String name, username, presentation, location, statusMessage,
            currentServer;
    private boolean allowFriendRequests, online, playing, friendStatus;
    private List<PlatoonData> platoons;

    // Other
    private StringBuilder personaString = new StringBuilder();
    private StringBuilder personaIdString = new StringBuilder();
    private StringBuilder personaPlatformString = new StringBuilder();
    private StringBuilder platoonIdString = new StringBuilder();

    // Construct(s)
    public ProfileInformation(

            int a, long uid, long dob, long l, long sc, PersonaData[] pe, String n, String u,
            String p, String loc, String s,
            String c, boolean af, boolean o, boolean pl,
            boolean fs, List<PlatoonData> pd

    ) {

        age = a;
        userId = uid;
        dateOfBirth = dob;
        lastlogin = l;
        statusMessageChanged = sc;
        persona = pe.clone();
        name = n;
        username = u;
        presentation = p;
        location = loc;
        statusMessage = s;
        currentServer = c;
        allowFriendRequests = af;
        online = o;
        playing = pl;
        friendStatus = fs;
        platoons = pd;

    }

    // Getters
    public int getAge() {
        return age;
    }

    public long getUserId() {
        return userId;
    }

    public long getDOB() {
        return dateOfBirth;
    }

    public String getLastLogin(Context c) {
        return PublicUtils.getRelativeDate(c, lastlogin,
                R.string.info_lastlogin);
    }

    public long getStatusMessageChanged() {
        return statusMessageChanged;
    }

    public PersonaData getPersona(int position) {
        return ((persona.length < position) ? persona[position]
                : persona[0]);
    }

    public PersonaData[] getAllPersonas() {
        return persona;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPresentation() {
        return presentation;
    }

    public String getLocation() {
        return location;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public boolean getAllowFriendRequests() {
        return allowFriendRequests;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isFriend() {
        return friendStatus;
    }

    public long getPlatoonId(int position) {
        return ((platoons.size() < position) ? platoons.get(position).getId()
                : platoons.get(0).getId());
    }

    public PlatoonData getPlatoon(int position) {

        return platoons.get(position);
    }

    public int getNumPersonas() {

        return persona.length;

    }

    public int getNumPlatoons() {

        return platoons.size();

    }

    public List<PlatoonData> getPlatoons() {
        return platoons;
    }

    public void generate() {

        // Reset
        personaIdString.setLength(0);
        personaString.setLength(0);
        personaPlatformString.setLength(0);
        platoonIdString.setLength(0);

        // Iterate
        for (PersonaData p : persona) {

            personaIdString.append(p.getId() + ":");
            personaString.append(p.getName() + ":");
            personaPlatformString.append(p.getPlatformId() + ":");

        }

        // Iterate
        for (PlatoonData p : platoons) {

            platoonIdString.append(p.getId() + ":");

        }

    }

    public final String[] toStringArray() {

        // Do we need to generate?
        if (personaIdString == null) {
            generate();
        }

        // Return it!
        return new String[] {

                age + "",
                userId + "",
                dateOfBirth + "",
                lastlogin + "",
                statusMessageChanged + "",
                name,
                username,
                (presentation == null) ? "" : presentation,
                (location == null) ? "" : location,
                (statusMessage == null) ? "" : statusMessage,
                (currentServer == null) ? "" : currentServer,
                personaIdString.toString(),
                personaString.toString(),
                personaPlatformString.toString(),
                allowFriendRequests ? "1" : "0",
                online ? "1" : "0",
                playing ? "1" : "0",
                friendStatus ? "1" : "0",
                platoonIdString.toString()

        };

    }

}
