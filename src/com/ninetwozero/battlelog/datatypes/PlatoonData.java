package com.ninetwozero.battlelog.datatypes;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class PlatoonData implements Serializable {

	//Attributes
	private static final long serialVersionUID = -1957801511187912007L;
	private long id;
	private int countFans, countMembers, platformId;
	private String name, tag;
	private byte[] image;
	private boolean visible;
	
	//Construct
	public PlatoonData(long i, int cF, int cM, int pId, String n, String t, byte[] img, boolean v) {
		
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
	public Bitmap getImage() { return (image != null ) ? BitmapFactory.decodeByteArray( image, 0, image.length) : null; }
	public boolean isVisible() { return this.visible; }
}
