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

import java.util.ArrayList;
import java.util.List;

public class PlatoonInformation {

    // Attributes
    private int platformId, gameId, numFans, numMembers, blazeClubId;
    private long id, date;
    private String name, tag, presentation, website;
    private boolean visible, isMember, isAdmin, allowNewMembers;
    private List<ProfileData> members, fans;
    private List<ProfileData> invitableFriends;
    private PlatoonStats stats;

    // Construct(s)
    public PlatoonInformation(

            long i, long d, int pId, int gId, int nF, int nM, int bcId, String n,
            String t, String p, String w, int v

    ) {

        id = i;
        date = d;
        platformId = pId;
        gameId = gId;
        numFans = nF;
        numMembers = nM;
        blazeClubId = bcId;
        name = n;
        tag = t;
        presentation = p;
        website = w;
        visible = (v == 1);

    }

    public PlatoonInformation(

            long i, long d, int pId, int g, int nF, int nM, int bcId, String n,
            String t, String p, String w, boolean v, boolean ism, boolean isa,
            boolean a, List<ProfileData> m,
            List<ProfileData> fa, List<ProfileData> pd,
            PlatoonStats st

    ) {

        platformId = pId;
        gameId = g;
        numFans = nF;
        numMembers = nM;
        blazeClubId = bcId;
        id = i;
        date = d;
        name = n;
        tag = t;
        presentation = p;
        website = w;
        visible = v;
        isMember = ism;
        isAdmin = isa;
        allowNewMembers = a;
        members = m;
        fans = fa;
        invitableFriends = pd;
        stats = st;

    }

    // Getters
    public int getPlatformId() {
        return platformId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getNumFans() {
        return numFans;
    }

    public int getNumMembers() {
        return numMembers;
    }

    public int getBlazeClubId() {
        return blazeClubId;
    }

    public boolean isMember() {
        return isMember;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getPresentation() {
        return presentation;
    }

    public String getWebsite() {
        return website;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isOpenForNewMembers() {
        return allowNewMembers;
    }

    public ArrayList<ProfileData> getMembers() {
        return (ArrayList<ProfileData>) members;
    }

    public ArrayList<ProfileData> getFans() {
        return (ArrayList<ProfileData>) fans;
    }

    public ArrayList<ProfileData> getInvitableFriends() {
        return (ArrayList<ProfileData>) invitableFriends;
    }

    public PlatoonStats getStats() {
        return stats;
    }

    public String[] toStringArray() {

        return new String[] {

                id + "", platformId + "", gameId + "",
                numFans + "", numMembers + "", blazeClubId + "",
                date + "", name, tag, presentation,
                website, (visible ? 1 : 0) + "",

        };

    }

}
