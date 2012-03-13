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

package com.ninetwozero.battlelog.datatypes;

import java.util.List;

import android.content.Context;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class ProfileInformation {

    // Attributes
    private int age;
    private long userId, dateOfBirth, lastlogin, statusMessageChanged;
    private long[] persona, platoonId;
    private int[] platform;
    private String name, username, presentation, location, statusMessage,
            currentServer;
    private String[] personaName;
    private boolean allowFriendRequests, online, playing, friendStatus;
    private List<PlatoonData> platoons;

    // Construct(s)
    public ProfileInformation(

            int a, long uid, long dob, long l, long sc, long[] pe, int[] pa,
            long[] plId, String n, String u, String p, String loc, String s,
            String c, String[] pn, boolean af, boolean o, boolean pl,
            boolean fs, List<PlatoonData> pd

    ) {

        this.age = a;
        this.userId = uid;
        this.dateOfBirth = dob;
        this.lastlogin = l;
        this.statusMessageChanged = sc;
        this.persona = pe;
        this.platform = pa;
        this.platoonId = plId;
        this.name = n;
        this.username = u;
        this.presentation = p;
        this.location = loc;
        this.statusMessage = s;
        this.currentServer = c;
        this.personaName = pn;
        this.allowFriendRequests = af;
        this.online = o;
        this.playing = pl;
        this.friendStatus = fs;
        this.platoons = pd;

    }

    // Getters
    public int getAge() {
        return this.age;
    }

    public long getUserId() {
        return this.userId;
    }

    public long getDOB() {
        return this.dateOfBirth;
    }

    public String getLastLogin(Context c) {
        return PublicUtils.getRelativeDate(c, this.lastlogin,
                R.string.info_lastlogin);
    }

    public long getStatusMessageChanged() {
        return this.statusMessageChanged;
    }

    public long getPersona(int position) {
        return ((this.persona.length < position) ? this.persona[position]
                : this.persona[0]);
    }

    public long[] getAllPersonas() {
        return this.persona;
    }

    public long getPlatform(int position) {
        return ((this.platform.length < position) ? this.platform[position]
                : this.platform[0]);
    }

    public int[] getAllPlatforms() {
        return this.platform;
    }

    public long getPlatoon(int position) {
        return ((this.platoonId.length < position) ? this.platoonId[position]
                : this.platoonId[0]);
    }

    public long[] getAllPlatoons() {
        return this.platoonId;
    }

    public int getNumPlatoons() {
        return this.platoonId.length;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPresentation() {
        return this.presentation;
    }

    public String getLocation() {
        return this.location;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public String getCurrentServer() {
        return this.currentServer;
    }

    public String getPersonaName(int position) {
        return ((this.personaName.length < position) ? this.personaName[position]
                : this.personaName[0]);
    }

    public String[] getAllPersonaNames() {
        return this.personaName;
    }

    public boolean getAllowFriendRequests() {
        return this.allowFriendRequests;
    }

    public boolean isOnline() {
        return this.online;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public boolean isFriend() {
        return this.friendStatus;
    }

    public List<PlatoonData> getPlatoons() {
        return this.platoons;
    }

    public String getPlatoonString() {

        String str = "";
        for (PlatoonData platoon : this.platoons) {
            str += platoon.getId() + ":";
        }
        return str;

    }

    public String getPersonaString() {

        String str = "";
        for (long personaId : this.persona) {
            str += personaId + ":";
        }
        return str;

    }

    public String getPersonaNameString() {

        String str = "";
        for (String name : this.personaName) {
            str += name + ":";
        }
        return str;

    }

    public String getPlatformString() {

        String str = "";
        for (long platformId : this.platform) {
            str += platformId + ":";
        }
        return str;

    }

    public final String[] toStringArray() {

        return new String[] {

                this.age + "",
                this.userId + "",
                this.dateOfBirth + "",
                this.lastlogin + "",
                this.statusMessageChanged + "",
                this.name,
                this.username,
                (this.presentation == null) ? "" : this.presentation,
                (this.location == null) ? "" : this.location,
                (this.statusMessage == null) ? "" : this.statusMessage,
                (this.currentServer == null) ? "" : this.currentServer,
                (this.getPersonaString() == null) ? "" : this
                        .getPersonaString(),
                (this.getPersonaNameString() == null) ? "" : this
                        .getPersonaNameString(),
                (this.getPlatformString() == null) ? "" : this
                        .getPlatformString(),
                this.allowFriendRequests ? "1" : "0", this.online ? "1" : "0",
                this.playing ? "1" : "0", this.friendStatus ? "1" : "0",
                this.getPlatoonString()

        };

    }

}
