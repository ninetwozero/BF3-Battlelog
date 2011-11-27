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

import com.ninetwozero.battlelog.misc.PublicUtils;

public class PlatoonInformation {

	//Attributes
	private int platformId, gameId, numFans, numMembers, blazeClubId;
	private long id, date;
	private String name, tag, presentation, pathToBadge, website;
	private boolean visible, hasAdminRights, allowNewMembers;
	private ArrayList<FeedItem> feedItems;
	private ArrayList<ProfileData> members;
	private ArrayList<ProfileData> fans;
	
	//Construct(s)
	public PlatoonInformation(
		
		int pId, int g, int nF, int nM, int bcId,
		long i, long d,
		String n, String t, String p, String pTB, String w,
		boolean v, boolean ham, boolean a,
		ArrayList<FeedItem> f, ArrayList<ProfileData> m, ArrayList<ProfileData> fa
		
	) {
		
		this.platformId = pId;
		this.gameId = g;
		this.numFans = nF;
		this.numMembers = nM;
		this.blazeClubId = bcId;
		this.id = i;
		this.date = d;
		this.name = n;
		this.tag = t;
		this.presentation = p;
		this.pathToBadge = pTB;
		this.website = w;
		this.visible = v;
		this.hasAdminRights = ham;
		this.allowNewMembers = a;
		this.feedItems = f;
		this.members = m;
		this.fans = fa;
	
	}
	
	//Getters
	public int getPlatformId() { return this.platformId; }
	public int getGameId() { return this.gameId; }
	public int getNumFans() { return this.numFans; }
	public int getNumMembers() { return this.numMembers; }
	public int getBlazeClubId() { return this.blazeClubId; }
	public boolean hasAdminRights() { return this.hasAdminRights; }
	public long getId() { return this.id; }
	public long getDate() { return this.date; }
	public String getName() { return this.name; }
	public String getTag() { return this.tag; }
	public String getPresentation() { return this.presentation; }
	public String getPathToBadge() { return this.pathToBadge; }
	public String getWebsite() { return this.website; }
	public boolean isVisible() { return this.visible; }
	public boolean isOpenForNewMembers() { return this.allowNewMembers; }
	public ArrayList<FeedItem> getFeedItems() { return this.feedItems; }
	public ArrayList<ProfileData> getMembers() { return this.members; }
	public ArrayList<ProfileData> getFans() { return this.fans; }

}
