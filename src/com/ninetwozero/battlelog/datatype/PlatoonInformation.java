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
    private int platformId;
    private int gameId;
    private int numFans;
    private int numMembers;
    private long id;
    private long blazeClubId;
    private long date;
    private String name;
    private String tag;
    private String presentation;
    private String website;
    private boolean visible;
    private boolean isMember;
    private boolean isAdmin;
    private boolean allowNewMembers;
    private List<ProfileData> members;
    private List<ProfileData> fans;
    private List<ProfileData> invitableFriends;
    private PlatoonStats stats;
    private long timestamp;

    // Construct(s)
    public PlatoonInformation(PlatoonInformation.Builder builder) {
        platformId = builder.platformId;
        gameId = builder.gameId;
        numFans = builder.numFans;
        numMembers = builder.numMembers;
        blazeClubId = builder.blazeClubId;
        id = builder.id;
        date = builder.date;
        name = builder.name;
        tag = builder.tag;
        presentation = builder.presentation;
        website = builder.website;
        visible = builder.visibility;
        isMember = builder.isMember;
        isAdmin = builder.isAdmin;
        allowNewMembers = builder.allowNewMembers;
        members = builder.members;
        fans = builder.fans;
        invitableFriends = builder.invitableFriends;
        stats = builder.stats;
        timestamp = builder.timestamp;
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

    public long getBlazeClubId() {
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

    public long getDateCreated() {
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
    
    public long getTimestamp() {
    	return timestamp;
    }
    
    public static class Builder {
    	private final long id;
        private final String name;
        private final String tag;
        
        // Optional
        public String presentation = "";
        public String website = "";
        public int platformId = 0;
        public int gameId = 0;
        public int numFans = 0;
        public int numMembers = 0;
        public long blazeClubId = 0;
        public long date = 0;
        public boolean visibility = true;
        public boolean isMember = false;
        public boolean isAdmin = false;
        public boolean allowNewMembers = false;
        public List<ProfileData> fans = new ArrayList<ProfileData>();
        public List<ProfileData> members = new ArrayList<ProfileData>();
        public List<ProfileData> invitableFriends = new ArrayList<ProfileData>();
        public PlatoonStats stats = null;
        public long timestamp = System.currentTimeMillis();
        
        public Builder(long i, String n, String t) {
            id = i;
            name = n;
            tag = t;
        }
        
        public Builder presentation(String s) {
        	presentation = s;
        	return this;
        }
        public Builder website(String s) {
        	website = s;
        	return this;
        }
        public Builder platformId(int n) {
        	platformId = n;
        	return this;
        }
        public Builder gameId(int n) {
        	gameId = n;
        	return this;
        }
        public Builder numFans(int n) {
        	numFans = n;
        	return this;
        }
        public Builder numMembers(int n) {
        	numMembers = n;
        	return this;
        }
        public Builder blazeClubId(long n) {
        	blazeClubId = n;
        	return this;
        }
        public Builder date(long n) {
        	date = n;
        	return this;
        }
        public Builder visibility(boolean b) {
        	visibility = b;
        	return this;
        }
        public Builder isMember(boolean b) {
        	isMember = b;
        	return this;
        }
        public Builder isAdmin(boolean b) {
        	isAdmin = b;
        	return this;
        }
        public Builder allowNewMembers(boolean b) {
        	allowNewMembers = b;
        	return this;
        }
        public Builder fans(List<ProfileData> l) {
        	fans = l;
        	return this;
        }
        public Builder members(List<ProfileData> l) {
        	members = l;
        	return this;
        }
        public Builder invitableFriends(List<ProfileData> l) {
        	invitableFriends = l;
        	return this;
        }
        public Builder stats(PlatoonStats s) {
        	stats = s;
        	return this;
        }
        public Builder timestamp(long n) {
        	timestamp = n;
        	return this;
        }
        public PlatoonInformation build() {
            return new PlatoonInformation(this);
        }
    }
}
