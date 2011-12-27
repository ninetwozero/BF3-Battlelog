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

import java.util.ArrayList;

import com.ninetwozero.battlelog.R;
import android.content.Context;

import com.ninetwozero.battlelog.misc.PublicUtils;

public class ProfileInformation {

	//Attributes
	private int age;
	private long userId, dateOfBirth, lastlogin, statusMessageChanged;
	private String name, username, presentation, location, statusMessage, currentServer;
	private boolean allowFriendRequests, online, playing, friendStatus;
	private ArrayList<FeedItem> feedItems;
	private ArrayList<PlatoonData> platoons;
	
	//Construct(s)
	public ProfileInformation(
		
		int a, long uid, long dob, long l, long sc,
		String n, String u, String p, String loc, String s, String c,
		boolean af, boolean o, boolean pl, boolean fs, ArrayList<FeedItem> f, ArrayList<PlatoonData> pd
		
	) {
		
		this.age = a;
		this.userId = uid;
		this.dateOfBirth = dob;
		this.lastlogin = l;
		this.statusMessageChanged = sc;
		this.name = n;
		this.username = u;
		this.presentation = p;
		this.location = loc; 
		this.statusMessage = s;
		this.currentServer = c;
		this.allowFriendRequests = af;
		this.online = o;
		this.playing = pl;
		this.friendStatus = fs;
		this.feedItems = f;
		this.platoons = pd;
	
	}
	
	//Getters
	public int getAge() { return this.age; }
	public long getUserId() { return this.userId; }
	public long getDOB() { return this.dateOfBirth; }
	public String getLastLogin(Context c) { return PublicUtils.getRelativeDate(c, this.lastlogin, R.string.info_lastlogin ); }
	public long getStatusMessageChanged() { return this.statusMessageChanged; }
	public String getName() { return this.name; }
	public String getUsername() { return this.username; }
	public String getPresentation() { return this.presentation; }
	public String getLocation() { return this.location; }
	public String getStatusMessage() { return this.statusMessage; }
	public String getCurrentServer() { return this.currentServer; }
	public boolean getAllowFriendRequests() { return this.allowFriendRequests; }
	public boolean isOnline() { return this.online; }
	public boolean isPlaying() { return this.playing; }
	public boolean isFriend() { return this.friendStatus; }
	public ArrayList<FeedItem> getFeedItems() { return this.feedItems; }
	public ArrayList<PlatoonData> getPlatoons() { return this.platoons; }
	public String getPlatoonString() { 
		
		String str = "";
		for(PlatoonData platoon : this.platoons ) { str += platoon.getId() + ":"; }
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
			this.presentation,
			this.location,
			this.statusMessage,
			this.currentServer,
			this.allowFriendRequests ? "1" : "0",
			this.online ? "1" : "0",
			this.playing ? "1" : "0",
			this.friendStatus ? "1" : "0",
			this.getPlatoonString()
			
		};
	
	}
	
}
