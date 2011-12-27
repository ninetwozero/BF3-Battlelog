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

import android.graphics.Bitmap;

import com.ninetwozero.battlelog.R;

public class PlatoonInformation {

	//Attributes
	private int platformId, gameId, numFans, numMembers, blazeClubId;
	private long id, date;
	private String name, tag, presentation, website;
	private Bitmap image;
	private boolean visible, isMember, isAdmin, allowNewMembers;
	private ArrayList<FeedItem> feedItems;
	private ArrayList<PlatoonMemberData> members, fans;
	private PlatoonStats stats;
	
	//Construct(s)
	public PlatoonInformation(
		
		int pId, int g, int nF, int nM, int bcId,
		long i, long d,
		String n, String t, String p, String w,
		Bitmap im, boolean v, boolean ism, boolean isa, boolean a,
		ArrayList<FeedItem> f, ArrayList<PlatoonMemberData> m, ArrayList<PlatoonMemberData> fa,
		PlatoonStats st
		
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
		this.website = w;
		this.image = im;
		this.visible = v;
		this.isMember = ism;
		this.isAdmin = isa;
		this.allowNewMembers = a;
		this.feedItems = f;
		this.members = m;
		this.fans = fa;
		this.stats = st;
	
	}
	
	//Getters
	public int getPlatformId() { return this.platformId; }
	public int getGameId() { return this.gameId; }
	public int getNumFans() { return this.numFans; }
	public int getNumMembers() { return this.numMembers; }
	public int getBlazeClubId() { return this.blazeClubId; }
	public boolean isMember() { return this.isMember; }
	public boolean isAdmin() { return this.isAdmin; }
	public long getId() { return this.id; }
	public long getDate() { return this.date; }
	public String getName() { return this.name; }
	public String getTag() { return this.tag; }
	public String getPresentation() { return this.presentation; }
	public boolean hasImage() { return (this.image != null ); }
	public Bitmap getImage() { return this.image; }
	public String getWebsite() { return this.website; }
	public boolean isVisible() { return this.visible; }
	public boolean isOpenForNewMembers() { return this.allowNewMembers; }
	public ArrayList<FeedItem> getFeedItems() { return this.feedItems; }
	public ArrayList<PlatoonMemberData> getMembers() { return this.members; }
	public ArrayList<PlatoonMemberData> getFans() { return this.fans; }
	public PlatoonStats getStats() { return this.stats; }

}
