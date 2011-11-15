package com.ninetwozero.battlelog.datatypes;

import android.graphics.drawable.Drawable;


public class PlatoonData {

	//Attributes
	private long id;
	private int countFans, countMembers, platformId;
	private String name, tag;
	private Drawable image;
	private boolean visible;
	
	//Construct
	public PlatoonData(long i, int cF, int cM, int pId, String n, String t, Drawable img, boolean v) {
		
		this.id = i;
		this.countFans = cF;
		this.countMembers = cM;
		this.platformId = pId;
		this.name = n;
		this.tag = t;
		this.image = img;
		this.visible = v;
		
	}
	
	//Getters
	public long getId() { return this.id; }
	public int getCountFans() { return this.countFans; }
	public int getCountMembers() { return this.countMembers; }
	public int getPlatformId() { return this.platformId; }
	public String getName() { return this.name; }
	public String getTag() { return this.tag; }
	public Drawable getImage() { return this.image; }
	public boolean isVisible() { return this.visible; }
}
