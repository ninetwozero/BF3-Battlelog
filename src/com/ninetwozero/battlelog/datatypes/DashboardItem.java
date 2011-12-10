package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;

public class DashboardItem {

	//Attributes
	private long id;
	private String title;
	
	//Construct
	public DashboardItem(long i, String t) {
		
		this.id = i;
		this.title = t;
		
	}
	
	//Getters
	public long getId() { return this.id; }
	public String getTitle() { return this.title; }
	
}
